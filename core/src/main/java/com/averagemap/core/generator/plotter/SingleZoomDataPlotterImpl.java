package com.averagemap.core.generator.plotter;

import com.averagemap.core.colorCalculator.ColorCalculator;
import com.averagemap.core.coordinates.model.*;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.Border;
import com.averagemap.core.coordinates.model.border.BorderInTile;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorder;
import com.averagemap.core.generator.filling.DefaultSquareFillingStrategy;
import com.averagemap.core.generator.filling.EmptySquareFillingStrategy;
import com.averagemap.core.generator.filling.FullSquareFillingStrategy;
import com.averagemap.core.generator.filling.SquareFillingStrategy;
import com.averagemap.core.images.ImageTile;
import com.averagemap.core.images.ImageTileSaver;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import com.averagemap.core.valueCalculator.factory.PointValueCalculatorFactory;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

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
//        GeneralPath outlinePath = createOutline(zoomSpecificBorder);
        tilesArea.stream()
                .parallel()
                .forEach(tile -> {
                    BufferedImage image = drawImage(tile, points, zoomSpecificBorder, minAndMaxValue);
                    ImageTile imageTile = new ImageTile(tile, image);
                    imageTileSaver.saveTile(imageTile);
                });
    }

    private BufferedImage drawImage(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> uniquePoints, ZoomSpecificBorder zoomSpecificBorder, Pair<Double, Double> minAndMaxValue) {
        BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, TYPE_INT_ARGB);
        Graphics2D graphics2D = drawEmptySquare(image);
        SquareFillingStrategy squareFillingStrategy = pickStrategy(tile, zoomSpecificBorder);
        Area drawingArea = getDrawingArea(tile, zoomSpecificBorder);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        squareFillingStrategy.fill(tile,
                pointValueCalculatorFactory,
                (PointValueCalculator pointValueCalculator, GoogleMapsPosition pixelPosition) -> drawPixel(image, pointValueCalculator, pixelPosition, minAndMaxValue),
                (GoogleMapsPosition pixelPosition) -> shouldDraw(drawingArea, pixelPosition));
        drawPointsAsPixels(tile, uniquePoints, graphics2D);
        return image;
    }

    private Graphics2D drawEmptySquare(BufferedImage image) {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint(new Color(0f, 0f, 0f, 0f));
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
        return graphics2D;
    }

    private Area getDrawingArea(GoogleMapsTile tile, GeneralPath outlinePath) {
        Area drawingArea = new Area(outlinePath);
        drawingArea.intersect(new Area(new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
        return drawingArea;
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

    private SquareFillingStrategy pickStrategy(GoogleMapsTile tile, ZoomSpecificBorder zoomSpecificBorder) {
        int left = tile.getX() * TILE_SIZE;
        int top = tile.getY() * TILE_SIZE;
        boolean wholeTileIsInOutlinePath = zoomSpecificBorder.contains(left, top, TILE_SIZE - 1, TILE_SIZE - 1);
        if (wholeTileIsInOutlinePath) {
            return new FullSquareFillingStrategy();
        } else if (zoomSpecificBorder.intersects(left, top, TILE_SIZE - 1, TILE_SIZE - 1)) {
            return new DefaultSquareFillingStrategy();
        } else {
            return new EmptySquareFillingStrategy();
        }
    }

    private boolean shouldDraw(Area outlinePath, GoogleMapsPosition pixelPosition) {
        return outlinePath.contains(pixelPosition.getX(), pixelPosition.getY());
    }

    private Void drawPixel(BufferedImage image, PointValueCalculator pointValueCalculatorForTile, GoogleMapsPosition pixelPosition, Pair<Double, Double> minAndMaxValue) {
        InSquarePosition position = new InSquarePosition(pixelPosition.getX() % TILE_SIZE, pixelPosition.getY() % TILE_SIZE);
        double averageValue = pointValueCalculatorForTile.calculate(pixelPosition);
        Color color = colorCalculator.calculate(averageValue, minAndMaxValue);
        image.setRGB(position.getX(), position.getY(), color.getRGB());
        return null;
    }

    private GeneralPath createOutline(List<GoogleMapsPosition> outline) {
        GeneralPath clip = new GeneralPath(Path2D.WIND_EVEN_ODD);
        GoogleMapsPosition first = outline.get(0);
        clip.moveTo(first.getX(), first.getY());
        outline.forEach(position -> clip.lineTo(position.getX(), position.getY()));
        clip.closePath();
        return clip;
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
