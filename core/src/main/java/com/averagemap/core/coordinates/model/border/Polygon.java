package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.Position2D;

import java.util.List;

public class Polygon<T extends Position2D> {

    private final LinearRing<T> exteriorRing;

    private final List<LinearRing<T>> holes;

    public Polygon(LinearRing<T> exteriorRing, List<LinearRing<T>> holes) {
        this.exteriorRing = exteriorRing;
        this.holes = holes;
    }

    public List<LinearRing<T>> getHoles() {
        return holes;
    }

    public LinearRing<T> getExteriorRing() {
        return exteriorRing;
    }
}
