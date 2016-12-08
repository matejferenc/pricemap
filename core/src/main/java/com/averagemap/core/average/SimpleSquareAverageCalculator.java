package com.averagemap.core.average;

import java.util.Collection;
import java.util.stream.Collectors;

import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;

public class SimpleSquareAverageCalculator implements AverageCalculator {

    @Override
    public <T extends Position2D> Collection<Point<T>> calculateAverages(Collection<Point<T>> points) {
        return points.stream()
                .map(pointWithValue -> new Point<T>(pointWithValue.getPosition(), getAverage(pointWithValue, points)))
                .collect(Collectors.toList());
    }

    private <T extends Position2D> double getAverage(Point<T> one, Collection<Point<T>> points) {
        return points.stream()
                .filter(pointWithValue -> Math.abs(pointWithValue.getPosition().getX().doubleValue() - one.getPosition().getX().doubleValue()) < 0.06)
                .filter(pointWithValue -> Math.abs(pointWithValue.getPosition().getY().doubleValue() - one.getPosition().getY().doubleValue()) < 0.06)
                .mapToDouble(Point::getValue)
                .average()
                .orElse(one.getValue());
    }
}
