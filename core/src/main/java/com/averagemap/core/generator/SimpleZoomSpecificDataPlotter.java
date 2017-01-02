package com.averagemap.core.generator;

import com.averagemap.core.coordinates.*;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.duplicate.DuplicateRemover;
import com.averagemap.core.images.ImageTile;
import com.averagemap.core.images.ImageTilesForOneZoom;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static com.averagemap.core.coordinates.CoordinatesUtils.getEncompassingArea;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.util.stream.Collectors.toList;

public class SimpleZoomSpecificDataPlotter implements ZoomSpecificDataPlotter {

    private GoogleMapsPosition topLeft;
    private Graphics2D graphics2d;
    private BufferedImage image;

    private final DuplicateRemover<Integer, GoogleMapsPosition> duplicateRemover;
    private Double min;
    private Double max;

    public SimpleZoomSpecificDataPlotter(DuplicateRemover<Integer, GoogleMapsPosition> duplicateRemover) {
        this.duplicateRemover = duplicateRemover;
    }

    @Override
    public ImageTilesForOneZoom plot(Collection<Point<GoogleMapsPosition>> points, List<GoogleMapsPosition> outline, int zoom) {
        Area<GoogleMapsTile> encompassingArea = getEncompassingArea(outline);
        calculateTopLeftPositionOfImage(encompassingArea);
        prepareForDrawing(encompassingArea);
        cropImage(outline);
        Collection<Point<GoogleMapsPosition>> uniquePoints = duplicateRemover.removeDuplicates(points);
        drawWholeImage(uniquePoints, image);
        Collection<ImageTile> tiles = cutImageIntoTiles(image, encompassingArea, zoom);
        return new ImageTilesForOneZoom(tiles, zoom);
    }

    private void prepareForDrawing(Area<GoogleMapsTile> encompassingArea) {
        image = new BufferedImage(encompassingArea.getWidth() * TILE_SIZE, encompassingArea.getHeight() * TILE_SIZE, TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
//        graphics2d.setComposite(AlphaComposite.Clear);
        graphics2d.setPaint(new Color(0f, 0f, 0f, 0f));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    private void calculateTopLeftPositionOfImage(Area<GoogleMapsTile> encompassingArea) {
        GoogleMapsTile encompassingAreaTopLeft = encompassingArea.getTopLeft();
        this.topLeft = new GoogleMapsPosition(
                encompassingAreaTopLeft.getX() * TILE_SIZE,
                encompassingAreaTopLeft.getY() * TILE_SIZE,
                encompassingAreaTopLeft.getZoom());
    }

    private void cropImage(List<GoogleMapsPosition> outline) {
        List<Position2D<Integer>> outlineInImage = calculateOutlineInImage(outline);
        GeneralPath clip = new GeneralPath(Path2D.WIND_EVEN_ODD);
        Position2D<Integer> start = outlineInImage.get(0);
        clip.moveTo(start.getX(), start.getY());
        outlineInImage.stream()
                .forEach(position -> clip.lineTo(position.getX(), position.getY()));
        clip.closePath();
        graphics2d.setClip(clip);
    }

    private List<Position2D<Integer>> calculateOutlineInImage(List<GoogleMapsPosition> outline) {
        return outline.stream()
                .map(this::transformToImagePosition)
                .distinct()
                .collect(toList());
    }

    private Position2D<Integer> transformToImagePosition(GoogleMapsPosition position) {
        return new GoogleMapsPosition(position.getX() - topLeft.getX(), position.getY() - topLeft.getY(), position.getZoom());
    }

    private Collection<ImageTile> cutImageIntoTiles(BufferedImage image, Area<GoogleMapsTile> encompassingArea, int zoom) {
        GoogleMapsTile topLeftTile = encompassingArea.getTopLeft();
        Collection<ImageTile> result = new ArrayList<>();
        for (int x = 0; x < encompassingArea.getWidth(); x++) {
            for (int y = 0; y < encompassingArea.getHeight(); y++) {
                ImageTile imageTile = createImageTile(image, topLeftTile, zoom, x, y);
                result.add(imageTile);
            }
        }
        return result;
    }

    private ImageTile createImageTile(BufferedImage image, GoogleMapsTile topLeftTile, int zoom, int x, int y) {
        BufferedImage subimage = image.getSubimage(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        GoogleMapsTile googleMapsTile = new GoogleMapsTile(topLeftTile.getX() + x, topLeftTile.getY() + y, zoom);
        return new ImageTile(googleMapsTile, subimage);
    }

    private void drawWholeImage(Collection<Point<GoogleMapsPosition>> points, BufferedImage image) {
        countMinAndMaxValue(points);
        Delaunay_Triangulation dt = new Delaunay_Triangulation();
        points.stream()
                .map(point -> {
                    Position2D<Integer> position2D = transformToImagePosition(point.getPosition());
                    return new Point_dt(position2D.getX(), position2D.getY(), point.getValue());
                })
                .forEach(dt::insertPoint);
        Iterator<Triangle_dt> iterator = dt.trianglesIterator();
        while (iterator.hasNext()) {
            Triangle_dt triangle = iterator.next();
            if (!triangle.isHalfplane()) {
                triangle.p1().getValue();
                Color color1 = calculateColor(triangle.p1().getValue());
                Color color2 = calculateColor(triangle.p2().getValue());
                Color color3 = calculateColor(triangle.p3().getValue());
                Color transparent = new Color(0, 0, 0, 0);
                Polygon polygon = new Polygon(
                        new int[]{(int) triangle.p1().x(), (int) triangle.p2().x(), (int) triangle.p3().x()},
                        new int[]{(int) triangle.p1().y(), (int) triangle.p2().y(), (int) triangle.p3().y()},
                        3);
                GradientPaint gradient1 = new GradientPaint(
                        (float) triangle.p1().x(), (float) triangle.p1().y(), color1,
                        (float) triangle.p2().x(), (float) triangle.p2().y(), transparent);
                GradientPaint gradient2 = new GradientPaint(
                        (float) triangle.p2().x(), (float) triangle.p2().y(), color2,
                        (float) triangle.p3().x(), (float) triangle.p3().y(), transparent);
                GradientPaint gradient3 = new GradientPaint(
                        (float) triangle.p3().x(), (float) triangle.p3().y(), color3,
                        (float) triangle.p1().x(), (float) triangle.p1().y(), transparent);
                graphics2d.setPaint(gradient1);
                graphics2d.fill(polygon);
                graphics2d.setPaint(gradient2);
                graphics2d.fill(polygon);
                graphics2d.setPaint(gradient3);
                graphics2d.fill(polygon);
            }
        }
//        graphics2d.setColor(Color.BLUE);
//        graphics2d.fill(new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
    }

    private void countMinAndMaxValue(Collection<Point<GoogleMapsPosition>> points) {
        min = points.stream()
                .map(Point::getValue)
                .min(Double::compare)
                .orElseThrow(() -> new IllegalArgumentException("wtf"));
        max = points.stream()
                .map(Point::getValue)
                .max(Double::compare)
                .orElseThrow(() -> new IllegalArgumentException("wtf"));
    }

    private Color calculateColor(double value) {
        float percent = (float) ((value - min) / (max - min));
        int r = (int) (255 * percent);
        int g = (int) (255 * (1 - percent));
        int b = 0;
        return new Color(r, g, b);
    }
}
