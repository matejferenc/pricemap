package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.Point;

import java.util.Collection;

public interface PointValueCalculator {

    double calculate(Collection<Point<GoogleMapsPosition>> points, GoogleMapsPosition pixelPosition);
}
