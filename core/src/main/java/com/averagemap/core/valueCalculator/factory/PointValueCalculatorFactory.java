package com.averagemap.core.valueCalculator.factory;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.valueCalculator.PointValueCalculator;

import java.util.Collection;

public interface PointValueCalculatorFactory {

    PointValueCalculator create(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> points);
}
