package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;

public class BorderInTile {

    private MultiPolygonInTile multiPolygonInTile;

    public BorderInTile(MultiPolygonInTile multiPolygonInTile) {
        this.multiPolygonInTile = multiPolygonInTile;
    }

    public boolean isFull(GoogleMapsTile tile) {
        return multiPolygonInTile.getPolygons().stream()
                .anyMatch(polygonInTile ->
                        polygonInTile.getExteriorRing().equals(CoordinatesUtils.toArea(tile))
                                &&
                                polygonInTile.getHoles().stream()
                                        .noneMatch(hole -> hole.contains(CoordinatesUtils.toRectangle2D(tile))
                                                || hole.intersects(CoordinatesUtils.toRectangle2D(tile)))
                );
    }

    public boolean isEmpty(GoogleMapsTile tile) {
//        return multiPolygonInTile.getPolygons().isEmpty();
        return multiPolygonInTile.getPolygons().isEmpty() || multiPolygonInTile.getPolygons().stream()
                .anyMatch(polygonInTile ->
                        polygonInTile.getExteriorRing().isEmpty()
                                ||
                                (polygonInTile.getExteriorRing().equals(CoordinatesUtils.toArea(tile))
                                        &&
                                        polygonInTile.getHoles().stream()
                                                .anyMatch(hole -> hole.equals(CoordinatesUtils.toArea(tile))))
                );
    }

    public boolean contains(GoogleMapsPosition pixelPosition) {
        return multiPolygonInTile.getPolygons().stream()
                .anyMatch(polygonInTile -> polygonInTile.getExteriorRing().contains(pixelPosition.getX(), pixelPosition.getY())
                        &&
                        polygonInTile.getHoles().stream()
                                .noneMatch(hole -> hole.contains(pixelPosition.getX(), pixelPosition.getY())));
    }
}
