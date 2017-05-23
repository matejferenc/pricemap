package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.Position2D;

import java.util.List;

public class MultiPolygon<T extends Position2D> {

    private final List<Polygon<T>> polygons;

    public MultiPolygon(List<Polygon<T>> polygons) {
        this.polygons = polygons;
    }

    public List<Polygon<T>> getPolygons() {
        return polygons;
    }
}
