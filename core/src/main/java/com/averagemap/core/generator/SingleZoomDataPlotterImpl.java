package com.averagemap.core.generator;

import com.averagemap.core.colorCalculator.ColorCalculator;
import com.averagemap.core.coordinates.*;
import com.averagemap.core.coordinates.Point;
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
import java.awt.geom.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static com.averagemap.core.coordinates.CoordinatesUtils.getEncompassingArea;
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
    public void plot(Collection<Point<GoogleMapsPosition>> points, List<GoogleMapsPosition> outline, int zoom) {
        TilesArea tilesArea = getEncompassingArea(outline);
        Pair<Double, Double> minAndMaxValue = countMinAndMaxValue(points);
        GeneralPath outlinePath = createOutline(outline);
        tilesArea.stream()
                .parallel()
                .forEach(tile -> {
                    BufferedImage image = drawImage(tile, points, outlinePath, minAndMaxValue);
                    ImageTile imageTile = new ImageTile(tile, image);
                    imageTileSaver.saveTile(imageTile);
                });
    }

    private BufferedImage drawImage(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> uniquePoints, GeneralPath outlinePath, Pair<Double, Double> minAndMaxValue) {
        BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint(new Color(0f, 0f, 0f, 0f));
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
        SquareFillingStrategy squareFillingStrategy = pickStrategy(tile, outlinePath);
        Area area = new Area(outlinePath);
        area.intersect(new Area(new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE)));
        PointValueCalculator pointValueCalculator = this.pointValueCalculatorFactory.create(tile, uniquePoints);
        squareFillingStrategy.fill(tile,
                (InSquarePosition position, GoogleMapsPosition pixelPosition) -> drawPixel(position, image, pointValueCalculator, pixelPosition, minAndMaxValue),
                (GoogleMapsPosition pixelPosition) -> shouldDraw(area, pixelPosition));
        return image;
    }

    private SquareFillingStrategy pickStrategy(GoogleMapsTile tile, GeneralPath outlinePath) {
        int left = tile.getX() * TILE_SIZE;
        int top = tile.getY() * TILE_SIZE;
        if (outlinePath.contains(left, top, TILE_SIZE - 1, TILE_SIZE - 1)) {
//            System.out.println("full");
            return new FullSquareFillingStrategy();
        } else if (outlinePath.intersects(left, top, TILE_SIZE - 1, TILE_SIZE - 1)) {
//            System.out.println("default");
            return new DefaultSquareFillingStrategy();
        } else {
//            System.out.println("empty");
            return new EmptySquareFillingStrategy();
        }
    }

    private boolean shouldDraw(Area outlinePath, GoogleMapsPosition pixelPosition) {
        return outlinePath.contains(pixelPosition.getX(), pixelPosition.getY());
    }

    private Void drawPixel(InSquarePosition position, BufferedImage image, PointValueCalculator pointValueCalculatorForTile, GoogleMapsPosition pixelPosition, Pair<Double, Double> minAndMaxValue) {
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
