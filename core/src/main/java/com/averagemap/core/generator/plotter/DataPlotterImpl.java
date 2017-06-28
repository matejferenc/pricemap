package com.averagemap.core.generator.plotter;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.Border;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorder;
import com.averagemap.core.duplicate.DuplicateRemover;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.latLngToPosition;
import static java.util.stream.Collectors.toList;

public class DataPlotterImpl implements DataPlotter {

    private final SingleZoomDataPlotter singleZoomDataPlotter;
    private final DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover;
    private final int minZoom;
    private final int maxZoom;

    public DataPlotterImpl(SingleZoomDataPlotter singleZoomDataPlotter,
                           DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover,
                           int minZoom, int maxZoom) {
        this.singleZoomDataPlotter = singleZoomDataPlotter;
        this.duplicatePointRemover = duplicatePointRemover;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    @Override
    public void plot(Collection<Point<LatLng>> points, Border border) {
        IntStream.rangeClosed(minZoom, maxZoom)
                .forEach(zoom -> {
                    long start = System.currentTimeMillis();
                    generateImageTilesForOneZoom(points, border, zoom);
                    long end = System.currentTimeMillis();
                    System.out.println("Zoom " + zoom + " execution time: " + (end - start) / 1000 + " seconds");
                });
    }

    private void generateImageTilesForOneZoom(Collection<Point<LatLng>> points, Border border, int zoom) {
        Collection<Point<GoogleMapsPosition>> pointList = duplicatePointRemover.removeDuplicatePoints(
                points.stream()
                        .map(point -> new Point<>(latLngToPosition(point.getPosition(), zoom), point.getValue()))
                        .collect(toList()));
        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(zoom);
        singleZoomDataPlotter.plot(pointList, zoomSpecificBorder, zoom);
    }
}