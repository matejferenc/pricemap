package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.Position2D;

import java.util.List;

public class LinearRing<T extends Position2D> {

    private final List<T> lineString;

    public LinearRing(List<T> lineString) {
        this.lineString = lineString;
//        if (lineString.size() < 4) {
//            throw new IllegalArgumentException("Linear ring has fewer than 4 points");
//        }
//        if (!same(lineString.get(0).getY().doubleValue(), lineString.get(lineString.size() - 1).getY().doubleValue())
//                || !same(lineString.get(0).getX().doubleValue(), lineString.get(lineString.size() - 1).getX().doubleValue())) {
//            throw new IllegalArgumentException("Linear ring is not closed - first and last point differ");
//        }
    }

//    private boolean same(double a, double b) {
//        return Math.abs(a - b) < 0.0000001;
//    }

    public List<T> getLineString() {
        return lineString;
    }
}
