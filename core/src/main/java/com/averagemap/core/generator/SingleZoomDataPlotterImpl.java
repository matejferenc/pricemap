package com.averagemap.core.generator;

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

    private Color c1 = new Color(196, 35, 40);
    private Color c2 = new Color(243, 89, 37);
    private Color c3 = new Color(251, 146, 31);
    private Color c4 = new Color(131, 199, 80);
    private Color c5 = new Color(56, 185, 69);
    private Color c6 = new Color(1, 103, 55);

    private final ImageTileSaver imageTileSaver;
    private final PointValueCalculator pointValueCalculator;

    public SingleZoomDataPlotterImpl(ImageTileSaver imageTileSaver, PointValueCalculator pointValueCalculator) {
        this.imageTileSaver = imageTileSaver;
        this.pointValueCalculator = pointValueCalculator;
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
        Color color = calculateColorLevel(averageValue, minAndMaxValue);
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

    private Position2D<Integer> transformToImagePosition(GoogleMapsPosition position, GoogleMapsPosition topLeft) {
        return new GoogleMapsPosition(position.getX() - topLeft.getX(), position.getY() - topLeft.getY(), position.getZoom());
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

    private Color calculateColor(double value, Pair<Double, Double> minAndMaxValue) {
        double min = minAndMaxValue.getKey();
        double max = minAndMaxValue.getValue();
        float percent = (float) ((value - min) / (max - min));
        int r = (int) (255 * percent);
        int g = (int) (255 * (1 - percent));
        int b = 0;
        return new Color(r, g, b);
    }

    private Color calculateColorLevel(double value, Pair<Double, Double> minAndMaxValue) {
        double min = minAndMaxValue.getKey();
        double max = minAndMaxValue.getValue();
        float percent = (float) ((value - min) / (max - min));
        if (percent < 0.10) return c6;
        else if (percent < 0.20) return c5;
        else if (percent < 0.35) return c4;
        else if (percent < 0.50) return c3;
        else if (percent < 0.65) return c2;
        else return c1;

    }
}
