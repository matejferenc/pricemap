package com.averagemap.core.coordinates.model.border.javaGeomImpl;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.border.BorderInTile;

import java.awt.geom.Area;
import java.util.List;

class JavaGeomBorderInTile implements BorderInTile {

    private MultiPolygonInTile multiPolygonInTile;

    JavaGeomBorderInTile(MultiPolygonInTile multiPolygonInTile) {
        this.multiPolygonInTile = multiPolygonInTile;
    }

    @Override
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

    @Override
    public boolean isEmpty(GoogleMapsTile tile) {
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

    @Override
    public boolean contains(GoogleMapsPosition pixelPosition) {
        return multiPolygonInTile.getPolygons().stream()
                .anyMatch(polygonInTile -> polygonInTile.getExteriorRing().contains(pixelPosition.getX(), pixelPosition.getY())
                        &&
                        polygonInTile.getHoles().stream()
                                .noneMatch(hole -> hole.contains(pixelPosition.getX(), pixelPosition.getY())));
    }

    private static class MultiPolygonInTile {

        private final List<PolygonInTile> polygons;

        MultiPolygonInTile(List<PolygonInTile> polygons) {
            this.polygons = polygons;
        }

        List<PolygonInTile> getPolygons() {
            return polygons;
        }
    }

    static class PolygonInTile {

        private final Area exteriorRing;

        private final List<Area> holes;

        PolygonInTile(Area exteriorRing, List<Area> holes) {
            this.exteriorRing = exteriorRing;
            this.holes = holes;
        }

        Area getExteriorRing() {
            return exteriorRing;
        }

        List<Area> getHoles() {
            return holes;
        }
    }

    static MultiPolygonInTile newMultiPolygon(List<PolygonInTile> polygons) {
        return new MultiPolygonInTile(polygons);
    }

    static PolygonInTile newPolygon(Area exteriorRing, List<Area> holes) {
        return new PolygonInTile(exteriorRing, holes);
    }
}
