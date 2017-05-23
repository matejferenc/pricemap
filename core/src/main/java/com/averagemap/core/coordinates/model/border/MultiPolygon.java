package com.averagemap.core.coordinates.model.border;

import java.util.List;

public class MultiPolygon {

    private final List<Polygon> polygons;

    public MultiPolygon(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }
}
