package com.averagemap.core.coordinates.model.border;

import java.util.List;

public class MultiPolygonInTile {

    private List<PolygonInTile> polygons;

    public MultiPolygonInTile(List<PolygonInTile> polygons) {
        this.polygons = polygons;
    }

    public List<PolygonInTile> getPolygons() {
        return polygons;
    }
}
