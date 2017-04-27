package com.averagemap.core.generator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.duplicate.DuplicateRemover;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.latLngToPosition;
import static java.util.stream.Collectors.toList;

public class SimpleDataPlotter implements DataPlotter {

    private SingleZoomDataPlotter zoomSpecificDataPlotter;
    private DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover;
    private DuplicateRemover<Integer, GoogleMapsPosition> duplicatePositionRemover;
    private int maxZoom;

    public SimpleDataPlotter(SingleZoomDataPlotter zoomSpecificDataPlotter,
                             DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover,
                             DuplicateRemover<Integer, GoogleMapsPosition> duplicatePositionRemover,
                             int maxZoom) {
        this.zoomSpecificDataPlotter = zoomSpecificDataPlotter;
        this.duplicatePointRemover = duplicatePointRemover;
        this.duplicatePositionRemover = duplicatePositionRemover;
        this.maxZoom = maxZoom;
    }

    @Override
    public void plot(Collection<Point<LatLng>> points, List<LatLng> outline) {
        IntStream.rangeClosed(0, maxZoom)
                .forEach(zoom -> {
                    long start = System.currentTimeMillis();
                    generateImageTilesForOneZoom(points, outline, zoom);
                    long end = System.currentTimeMillis();
                    System.out.println("Zoom " + zoom + " execution time: " + (end - start) / 1000 + " seconds");
                });
    }

    private void generateImageTilesForOneZoom(Collection<Point<LatLng>> points, List<LatLng> outline, int zoom) {
        Collection<Point<GoogleMapsPosition>> pointList = duplicatePointRemover.removeDuplicatePoints(
                points.stream()
                        .map(point -> new Point<>(latLngToPosition(point.getPosition(), zoom), point.getValue()))
                        .collect(toList()));
        List<GoogleMapsPosition> zoomSpecificOutline = duplicatePositionRemover.removeDuplicatePositions(
                outline.stream()
                        .map(position -> latLngToPosition(position, zoom))
                        .collect(toList()));
        zoomSpecificDataPlotter.plot(pointList, zoomSpecificOutline, zoom);
    }
}