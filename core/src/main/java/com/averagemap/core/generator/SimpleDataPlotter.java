package com.averagemap.core.generator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.images.ImageTilesForEveryZoom;
import com.averagemap.core.images.ImageTilesForOneZoom;

import java.util.Collection;
import java.util.List;

import static com.averagemap.core.coordinates.CoordinatesUtils.latLngToPosition;
import static java.util.stream.Collectors.toList;

public class SimpleDataPlotter implements DataPlotter {

    ZoomSpecificDataPlotter zoomSpecificDataPlotter;

    public SimpleDataPlotter(ZoomSpecificDataPlotter zoomSpecificDataPlotter) {
        this.zoomSpecificDataPlotter = zoomSpecificDataPlotter;
    }

    @Override
    public ImageTilesForEveryZoom plot(Collection<Point<LatLng>> points, List<LatLng> outline) {
        ImageTilesForEveryZoom imageTilesForEveryZoom = new ImageTilesForEveryZoom();
        for (int zoom = 0; zoom < 14; zoom++) {
            ImageTilesForOneZoom imageTilesForOneZoom = generateImageTilesForOneZoom(points, outline, zoom);
            imageTilesForEveryZoom.addImageTiles(imageTilesForOneZoom);
        }
        return imageTilesForEveryZoom;
    }

    private ImageTilesForOneZoom generateImageTilesForOneZoom(Collection<Point<LatLng>> points, List<LatLng> outline, int zoom) {
        List<Point<GoogleMapsPosition>> pointList = points.stream()
                .map(point -> new Point<>(latLngToPosition(point.getPosition(), zoom), point.getValue()))
                .collect(toList());
        List<GoogleMapsPosition> zoomSpecificOutline = outline.stream()
                .map(position -> latLngToPosition(position, zoom))
                .collect(toList());
        return zoomSpecificDataPlotter.plot(pointList, zoomSpecificOutline, zoom);
    }
}