package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;

import java.util.Collection;

public class NearestNeighbor implements PointValueCalculator {

    private final Distance distance;

    public NearestNeighbor(Distance distance) {
        this.distance = distance;
    }

    @Override
    public double calculate(Collection<Point<GoogleMapsPosition>> points, GoogleMapsPosition pixelPosition) {
        final double[] minDistance = {Double.MAX_VALUE};
        final double[] value = new double[1];
        points.stream()
                .parallel()
                .forEach(point -> {
                    double distance = this.distance.distance(point.getPosition(), pixelPosition);
                    if (distance < minDistance[0]) {
                        minDistance[0] = distance;
                        value[0] = point.getValue();
                    }
                });
        return value[0];
    }
}
