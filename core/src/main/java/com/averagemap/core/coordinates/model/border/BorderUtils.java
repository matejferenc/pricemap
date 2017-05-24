package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.TilesArea;

import java.util.Collection;
import java.util.List;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.stream.Collectors.toList;

public class BorderUtils {

    public static TilesArea getEncompassingArea(MultiPolygon<GoogleMapsPosition> border) {
        final int zoom = assertZoomLevelIsTheSame(border);
        int left = MAX_VALUE;
        int top = MAX_VALUE;
        int right = MIN_VALUE;
        int bottom = MIN_VALUE;
        for (Polygon<GoogleMapsPosition> polygon : border.getPolygons()) {
            for (GoogleMapsPosition borderPoint : polygon.getExteriorRing().getLineString()) {
                int x = borderPoint.getX();
                if (x < left) {
                    left = x;
                } else if (x > right) {
                    right = x;
                }
                int y = borderPoint.getY();
                if (y < top) {
                    top = y;
                } else if (y > bottom) {
                    bottom = y;
                }
            }
        }
        return new TilesArea(
                CoordinatesUtils.positionToTile(new GoogleMapsPosition(left, top, zoom)),
                CoordinatesUtils.positionToTile(new GoogleMapsPosition(right, bottom, zoom)));
    }

    private static int assertZoomLevelIsTheSame(MultiPolygon<GoogleMapsPosition> border) {
        Integer zoom = null;
        for (Polygon<GoogleMapsPosition> polygon : border.getPolygons()) {
            for (GoogleMapsPosition borderPoint : polygon.getExteriorRing().getLineString()) {
                if (zoom == null) {
                    zoom = borderPoint.getZoom();
                } else {
                    if (zoom != borderPoint.getZoom()) {
                        throw new IllegalArgumentException("zoom levels different");
                    }
                }
            }
        }
        if (zoom == null ){
            throw new IllegalArgumentException("empty");
        }
        return zoom;
    }

    public static MultiPolygon<GoogleMapsPosition> latLngToPosition(MultiPolygon<LatLng> multiPolygon, int zoom) {
        return new MultiPolygon<>(multiPolygon.getPolygons().stream()
                .map(polygon -> new Polygon<>(latLngToPosition(polygon.getExteriorRing(), zoom),
                        polygon.getHoles().stream()
                                .map(hole -> latLngToPosition(hole, zoom))
                                .collect(toList()))
                )
                .collect(toList()));
    }

    private static LinearRing<GoogleMapsPosition> latLngToPosition(LinearRing<LatLng> ring, int zoom) {
        return new LinearRing<>(
                ring.getLineString().stream()
                        .map(latLng -> CoordinatesUtils.latLngToPosition(latLng, zoom))
                        .collect(toList()));
    }
}
