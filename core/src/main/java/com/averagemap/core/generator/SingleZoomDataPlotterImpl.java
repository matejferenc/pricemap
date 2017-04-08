package com.averagemap.core.generator;

import com.averagemap.core.coordinates.*;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.images.ImageTile;
import com.averagemap.core.images.ImageTileSaver;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static com.averagemap.core.coordinates.CoordinatesUtils.getEncompassingArea;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class SingleZoomDataPlotterImpl implements SingleZoomDataPlotter {

    private GoogleMapsPosition topLeft;
    private Graphics2D graphics2d;
    private BufferedImage image;

    private ImageTileSaver imageTileSaver;
    private Distance distance;

    public SingleZoomDataPlotterImpl(ImageTileSaver imageTileSaver, Distance distance) {
        this.imageTileSaver = imageTileSaver;
        this.distance = distance;
    }

    @Override
    public void plot(Collection<Point<GoogleMapsPosition>> points, List<GoogleMapsPosition> outline, int zoom) {
        TilesArea tilesArea = getEncompassingArea(outline);
        calculateTopLeftPositionOfImage(tilesArea);
        prepareForDrawing(tilesArea);
        Pair<Double, Double> minAndMaxValue = countMinAndMaxValue(points);
        GeneralPath outlinePath = createOutline(outline);
        tilesArea.stream()
                .parallel()
                .forEach(tile -> {
                    BufferedImage image = drawImage(tile, points, outlinePath, minAndMaxValue);
                    ImageTile imageTile = new ImageTile(tile, image);
                    imageTileSaver.saveTile(imageTile);
                });
        drawWholeImage(points, image);
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
        double averageValue = knn(points, pixelPosition);
        Color color = calculateColor(averageValue, minAndMaxValue);
        image.setRGB(i, j, color.getRGB());
    }

    private double knn(Collection<Point<GoogleMapsPosition>> uniquePoints, GoogleMapsPosition pixelPosition) {
        final double[] minDistance = {Double.MAX_VALUE};
        final double[] value = new double[1];
        uniquePoints.stream()
                .parallel()
                .forEach(point -> {
                    double distance = this.distance.distance(point.getPosition(), pixelPosition);
                    if (distance < minDistance[0]) {
                        minDistance[0] = distance;
                        value[0] = point.getValue();
                    }
                });
        return value[0];
    }

    private void prepareForDrawing(TilesArea encompassingArea) {
        image = new BufferedImage(encompassingArea.getWidth() * TILE_SIZE, encompassingArea.getHeight() * TILE_SIZE, TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
        graphics2d.setPaint(new Color(0f, 0f, 0f, 0f));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    private void calculateTopLeftPositionOfImage(TilesArea encompassingArea) {
        GoogleMapsTile encompassingAreaTopLeft = encompassingArea.getTopLeft();
        this.topLeft = new GoogleMapsPosition(
                encompassingAreaTopLeft.getX() * TILE_SIZE,
                encompassingAreaTopLeft.getY() * TILE_SIZE,
                encompassingAreaTopLeft.getZoom());
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

    private void drawWholeImage(Collection<Point<GoogleMapsPosition>> points, BufferedImage image) {
        points.stream()
                .map(point -> {
                    return transformToImagePosition(point.getPosition(), topLeft);
                });
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
}
