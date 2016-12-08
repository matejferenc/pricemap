package com.averagemap.core.generator;

import com.averagemap.core.coordinates.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleDataPlotter implements DataPlotter {

    ZoomSpecificDataPlotter zoomSpecificDataPlotter;

    public SimpleDataPlotter(ZoomSpecificDataPlotter zoomSpecificDataPlotter) {
        this.zoomSpecificDataPlotter = zoomSpecificDataPlotter;
    }

    @Override
    public void plot(Collection<Point<LatLng>> points) {
        for (int zoom = 8; zoom < 16; zoom++) {
            final int finalZoom = zoom;
            List<Point<GoogleMapsPosition>> pointList = points.stream()
                    .map(point -> new Point<>(CoordinatesUtils.latLngToTile(point.getPosition(), finalZoom), point.getValue()))
                    .collect(Collectors.toList());
            Area<GoogleMapsPosition> zoomSpecificArea = CoordinatesUtils.getZoomSpecificArea(points);
            zoomSpecificDataPlotter.plot(pointList, zoomSpecificArea);
        }
    }
}
