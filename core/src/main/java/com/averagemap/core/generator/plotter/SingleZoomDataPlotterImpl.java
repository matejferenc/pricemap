package com.averagemap.core.generator.plotter;

import com.averagemap.core.colorCalculator.ColorCalculator;
import com.averagemap.core.coordinates.model.*;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.BorderInTile;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorder;
import com.averagemap.core.generator.filling.DefaultSquareFillingStrategy;
import com.averagemap.core.generator.filling.EmptySquareFillingStrategy;
import com.averagemap.core.generator.filling.FullSquareFillingStrategy;
import com.averagemap.core.generator.filling.SquareFillingStrategy;
import com.averagemap.core.images.ImageTile;
import com.averagemap.core.images.ImageTileSaver;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import com.averagemap.core.valueCalculator.PointValueCalculatorFactory;
import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class SingleZoomDataPlotterImpl implements SingleZoomDataPlotter {

    private final ImageTileSaver imageTileSaver;
    private final PointValueCalculatorFactory pointValueCalculatorFactory;
    private final ColorCalculator colorCalculator;

    public SingleZoomDataPlotterImpl(ImageTileSaver imageTileSaver, PointValueCalculatorFactory pointValueCalculatorFactory, ColorCalculator colorCalculator) {
        this.imageTileSaver = imageTileSaver;
        this.pointValueCalculatorFactory = pointValueCalculatorFactory;
        this.colorCalculator = colorCalculator;
    }

    @Override
    public void plot(Collection<Point<GoogleMapsPosition>> points, ZoomSpecificBorder zoomSpecificBorder, int zoom) {
        TilesArea tilesArea = zoomSpecificBorder.getEncompassingArea();
        Pair<Double, Double> minAndMaxValue = countMinAndMaxValue(points);
        pointValueCalculatorFactory.setUp(points);
        tilesArea.stream()
                .parallel()
                .forEach(tile -> {
                    BufferedImage image = drawImage(tile, points, zoomSpecificBorder, minAndMaxValue);
                    ImageTile imageTile = new ImageTile(tile, image);
                    imageTileSaver.saveTile(imageTile);
                });
    }

    private BufferedImage drawImage(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> points, ZoomSpecificBorder zoomSpecificBorder, Pair<Double, Double> minAndMaxValue) {
        BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, TYPE_INT_ARGB);
        Graphics2D graphics2D = drawEmptySquare(image);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        SquareFillingStrategy squareFillingStrategy = pickStrategy(borderInTile);
        squareFillingStrategy.fill(tile,
                pointValueCalculatorFactory,
                (PointValueCalculator pointValueCalculator, GoogleMapsPosition pixelPosition) -> drawPixel(image, pointValueCalculator, pixelPosition, minAndMaxValue),
                (GoogleMapsPosition pixelPosition) -> shouldDraw(borderInTile, pixelPosition));
//        drawPointsAsPixels(tile, points, graphics2D);
        return image;
    }

    private Graphics2D drawEmptySquare(BufferedImage image) {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint(new Color(0f, 0f, 0f, 0f));
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
        return graphics2D;
    }

    private void drawPointsAsPixels(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> uniquePoints, Graphics2D graphics2D) {
        graphics2D.setColor(Color.RED);
        uniquePoints.stream()
                .filter(point -> point.getPosition().getX() >= tile.getX() * TILE_SIZE)
                .filter(point -> point.getPosition().getX() < tile.getX() * TILE_SIZE + TILE_SIZE)
                .filter(point -> point.getPosition().getY() >= tile.getY() * TILE_SIZE)
                .filter(point -> point.getPosition().getY() < tile.getY() * TILE_SIZE + TILE_SIZE)
                .forEach(point -> {
                    graphics2D.fillRect(point.getPosition().getX() - tile.getX() * TILE_SIZE - 1,
                            point.getPosition().getY() - tile.getY() * TILE_SIZE - 1,
                            3,
                            3);
                });
    }

    private SquareFillingStrategy pickStrategy(BorderInTile borderInTile) {
        if (borderInTile.isFull()) {
            return new FullSquareFillingStrategy();
        } else if (borderInTile.isEmpty()) {
            return new EmptySquareFillingStrategy();
        } else {
            return new DefaultSquareFillingStrategy();
        }
    }

    private boolean shouldDraw(BorderInTile borderInTile, GoogleMapsPosition pixelPosition) {
        return borderInTile.contains(pixelPosition);
    }

    private Void drawPixel(BufferedImage image, PointValueCalculator pointValueCalculatorForTile, GoogleMapsPosition pixelPosition, Pair<Double, Double> minAndMaxValue) {
        InSquarePosition position = new InSquarePosition(pixelPosition.getX() % TILE_SIZE, pixelPosition.getY() % TILE_SIZE);
        double averageValue = pointValueCalculatorForTile.calculate(pixelPosition);
        Color color = colorCalculator.calculate(averageValue, minAndMaxValue);
        image.setRGB(position.getX(), position.getY(), color.getRGB());
        return null;
    }

    private Pair<Double, Double> countMinAndMaxValue(Collection<Point<GoogleMapsPosition>> points) {
        double min = points.stream()
                .map(Point::getValue)
                .min(Double::compare)
                .orElseThrow(() -> new IllegalArgumentException("wtf"));
        double max = points.stream()
                .map(Point::getValue)
                .max(Double::compare)
                .orElseThrow(() -> new IllegalArgumentException("wtf"));
        return new Pair<>(min, max);
    }

}
