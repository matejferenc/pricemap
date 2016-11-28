package com.averagemap.core.average;

import java.util.Collection;
import java.util.stream.Collectors;

import com.averagemap.core.coordinates.PointWithValue;

public class SimpleSquareAverageCalculator implements AverageCalculator {

    @Override
    public Collection<PointWithValue> calculateAverages(Collection<PointWithValue> pointWithValues) {
        return pointWithValues.stream()
                .map(pointWithValue -> new PointWithValue(pointWithValue.getLatLng(), getAverage(pointWithValue, pointWithValues)))
                .collect(Collectors.toList());
    }

    private double getAverage(PointWithValue one, Collection<PointWithValue> pointWithValues) {
        return pointWithValues.stream()
                .filter(pointWithValue -> Math.abs(pointWithValue.getLatLng().getLat() - one.getLatLng().getLat()) < 0.06)
                .filter(pointWithValue -> Math.abs(pointWithValue.getLatLng().getLng() - one.getLatLng().getLng()) < 0.06)
                .mapToDouble(PointWithValue::getValue)
                .average()
                .orElse(one.getValue());
    }
}
