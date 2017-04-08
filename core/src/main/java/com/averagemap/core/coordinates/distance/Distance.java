package com.averagemap.core.coordinates.distance;

import com.averagemap.core.coordinates.GoogleMapsPosition;

public interface Distance {

    double distance(GoogleMapsPosition a, GoogleMapsPosition b);
}
