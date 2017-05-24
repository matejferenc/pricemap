package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.TilesArea;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class ZoomSpecificBorder {

    private final MultiPolygon<GoogleMapsPosition> zoomSpecificMultiPolygon;

    public ZoomSpecificBorder(MultiPolygon<GoogleMapsPosition> zoomSpecificMultiPolygon) {
        this.zoomSpecificMultiPolygon = zoomSpecificMultiPolygon;
    }

    public TilesArea getEncompassingArea() {
        final int zoom = assertZoomLevelIsTheSame(zoomSpecificMultiPolygon);
        int left = MAX_VALUE;
        int top = MAX_VALUE;
        int right = MIN_VALUE;
        int bottom = MIN_VALUE;
        for (Polygon<GoogleMapsPosition> polygon : zoomSpecificMultiPolygon.getPolygons()) {
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

    private int assertZoomLevelIsTheSame(MultiPolygon<GoogleMapsPosition> border) {
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
}
