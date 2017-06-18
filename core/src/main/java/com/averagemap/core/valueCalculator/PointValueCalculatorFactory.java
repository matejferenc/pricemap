package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.valueCalculator.PointValueCalculator;

import java.util.Collection;

public interface PointValueCalculatorFactory {

    void setUp(Collection<Point<GoogleMapsPosition>> points);

    PointValueCalculator create(GoogleMapsTile tile);
}
