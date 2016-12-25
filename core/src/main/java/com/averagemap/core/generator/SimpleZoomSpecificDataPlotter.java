package com.averagemap.core.generator;

import com.averagemap.core.coordinates.*;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.images.ImageTile;
import com.averagemap.core.images.ImageTilesForOneZoom;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static java.util.stream.Collectors.toList;

public class SimpleZoomSpecificDataPlotter implements ZoomSpecificDataPlotter {

    private GoogleMapsPosition topLeft;
    private Graphics2D graphics2d;
    private BufferedImage image;

    @Override
    public ImageTilesForOneZoom plot(Collection<Point<GoogleMapsPosition>> points, List<GoogleMapsPosition> outline, int zoom) {
        Area<GoogleMapsTile> encompassingArea = calculateEncompassingArea(outline);
        calculateTopLeftPositionOfImage(encompassingArea);
        prepareForDrawing(encompassingArea);
        cropImage(outline);
        drawWholeImage(points, image);
        Collection<ImageTile> tiles = cutImageIntoTiles(image, encompassingArea, zoom);
        return new ImageTilesForOneZoom(tiles, zoom);
    }

    private void prepareForDrawing(Area<GoogleMapsTile> encompassingArea) {
        image = new BufferedImage(encompassingArea.getWidth() * TILE_SIZE, encompassingArea.getHeight() * TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
//        graphics2d.setComposite(AlphaComposite.Clear);
        graphics2d.setPaint(new Color(0f, 0f, 0f, 0f));
        graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    private Area<GoogleMapsTile> calculateEncompassingArea(List<GoogleMapsPosition> points) {
        List<GoogleMapsPosition> positions = points.stream()
//                .map(Point::getPosition)
                .collect(toList());
        return CoordinatesUtils.getEncompassingArea(positions);
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
        graphics2d.setColor(Color.BLUE);
        graphics2d.fill(new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
    }
}
