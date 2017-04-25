package com.averagemap.core.generator;

import com.averagemap.core.colorCalculator.ColorCalculator;
import com.averagemap.core.coordinates.*;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.images.ImageTile;
import com.averagemap.core.images.ImageTileSaver;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static com.averagemap.core.coordinates.CoordinatesUtils.getEncompassingArea;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class SingleZoomDataPlotterImpl implements SingleZoomDataPlotter {

    private final ImageTileSaver imageTileSaver;
    private final PointValueCalculator pointValueCalculator;
    private final ColorCalculator colorCalculator;

    public SingleZoomDataPlotterImpl(ImageTileSaver imageTileSaver, PointValueCalculator pointValueCalculator, ColorCalculator colorCalculator) {
        this.imageTileSaver = imageTileSaver;
        this.pointValueCalculator = pointValueCalculator;
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
        IntStream.range(0, image.getWidth())
                .parallel()
                .forEach(i -> {
                    IntStream.range(0, image.getHeight())
                            .parallel()
                            .forEach(j -> {
                                GoogleMapsPosition pixelPosition = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE + j, tile.getZoom());
                                if (shouldDraw(outlinePath, pixelPosition)) {
                                    drawPixel(i, j, image, uniquePoints, pixelPosition, minAndMaxValue);
                                }
                            });
                });
        return image;
    }

    private boolean shouldDraw(GeneralPath outlinePath, GoogleMapsPosition pixelPosition) {
        return outlinePath.contains(pixelPosition.getX(), pixelPosition.getY());
    }

    private void drawPixel(int i, int j, BufferedImage image, Collection<Point<GoogleMapsPosition>> points, GoogleMapsPosition pixelPosition, Pair<Double, Double> minAndMaxValue) {
        double averageValue = pointValueCalculator.calculate(points, pixelPosition);
        Color color = colorCalculator.calculate(averageValue, minAndMaxValue);
        image.setRGB(i, j, color.getRGB());
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
