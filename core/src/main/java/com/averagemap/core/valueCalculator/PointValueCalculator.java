package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.coordinates.Point;

import java.util.Collection;

public interface PointValueCalculator {

    double calculate(GoogleMapsPosition pixelPosition);

    PointValueCalculator prepareForTile(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> points);
}
