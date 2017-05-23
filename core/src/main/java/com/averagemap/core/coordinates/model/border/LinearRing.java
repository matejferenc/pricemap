package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.LatLng;

import java.util.List;

public class LinearRing {

    private final List<LatLng> lineString;

    public LinearRing(List<LatLng> lineString) {
        this.lineString = lineString;
        if (lineString.size() < 4) {
            throw new IllegalArgumentException("Linear ring has fewer than 4 points");
        }
        if (!same(lineString.get(0).getLat(), lineString.get(lineString.size() - 1).getLat())
                || !same(lineString.get(0).getLng(), lineString.get(lineString.size() - 1).getLng())) {
            throw new IllegalArgumentException("Linear ring is not closed - first and last point differ");
        }
    }

    private boolean same(double a, double b) {
        return Math.abs(a - b) < 0.0000001;
    }

    public List<LatLng> getLineString() {
        return lineString;
    }
}
