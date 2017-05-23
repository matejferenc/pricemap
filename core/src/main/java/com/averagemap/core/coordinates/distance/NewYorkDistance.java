package com.averagemap.core.coordinates.distance;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;

public class NewYorkDistance implements Distance {

    @Override
    public double distance(GoogleMapsPosition a, GoogleMapsPosition b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
}
