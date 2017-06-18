package com.averagemap.core.valueCalculator.nearestNeighbor;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.valueCalculator.PointValueCalculatorFactory;
import com.averagemap.core.valueCalculator.PointValueCalculator;

import java.util.Collection;

public class NearestNeighborFactory implements PointValueCalculatorFactory {

    private final Distance distance;
    private Collection<Point<GoogleMapsPosition>> points;


    public NearestNeighborFactory(Distance distance) {
        this.distance = distance;
    }

    @Override
    public void setUp(Collection<Point<GoogleMapsPosition>> points) {
        this.points = points;
    }

    @Override
    public PointValueCalculator create(GoogleMapsTile tile) {
        //TODO: possibly limit points to square surroundings, if this implementation is ever going to be used
        return new NearestNeighbor(distance, points);
    }
}
