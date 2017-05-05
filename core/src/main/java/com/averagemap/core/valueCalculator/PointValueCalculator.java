package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.GoogleMapsPosition;

public interface PointValueCalculator {

    double calculate(GoogleMapsPosition pixelPosition);

}
