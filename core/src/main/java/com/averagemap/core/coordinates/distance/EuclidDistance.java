package com.averagemap.core.coordinates.distance;

import com.averagemap.core.coordinates.GoogleMapsPosition;

public class EuclidDistance implements Distance {

    @Override
    public double distance(GoogleMapsPosition a, GoogleMapsPosition b) {
        int i = a.getX() - b.getX();
        int j = a.getY() - b.getY();
        return Math.sqrt(i * i + j * j);
    }
}
