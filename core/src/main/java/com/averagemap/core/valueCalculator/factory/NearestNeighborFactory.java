package com.averagemap.core.valueCalculator.factory;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.valueCalculator.NearestNeighbor;
import com.averagemap.core.valueCalculator.PointValueCalculator;

import java.util.Collection;

public class NearestNeighborFactory implements PointValueCalculatorFactory {

    private final Distance distance;

    public NearestNeighborFactory(Distance distance) {
        this.distance = distance;
    }

    @Override
    public PointValueCalculator create(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> points) {
        //TODO: possibly limit points to square surroundings, if this implementation is ever going to be used
        return new NearestNeighbor(distance, points);
    }
}
