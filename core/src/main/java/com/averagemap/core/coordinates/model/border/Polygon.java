package com.averagemap.core.coordinates.model.border;

import java.util.List;

public class Polygon {

    private final LinearRing exteriorRing;

    private final List<LinearRing> holes;

    public Polygon(LinearRing exteriorRing, List<LinearRing> holes) {
        this.exteriorRing = exteriorRing;
        this.holes = holes;
    }

    public List<LinearRing> getHoles() {
        return holes;
    }

    public LinearRing getExteriorRing() {
        return exteriorRing;
    }
}
