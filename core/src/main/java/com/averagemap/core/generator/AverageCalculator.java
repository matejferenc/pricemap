package com.averagemap.core.generator;

import java.util.Collection;
import java.util.stream.Collectors;

public class AverageCalculator {

    public Collection<PointWithValue> calculateAverages(Collection<PointWithValue> pointWithValues) {
        return pointWithValues.stream()
                .map(pointWithValue -> new PointWithValue(pointWithValue.getLat(), pointWithValue.getLng(), getAverage(pointWithValue, pointWithValues)))
                .collect(Collectors.toList());
    }

    private double getAverage(PointWithValue one, Collection<PointWithValue> pointWithValues) {
        return pointWithValues.stream()
                .filter(pointWithValue -> Math.abs(pointWithValue.getLat() - one.getLat()) < 0.06)
                .filter(pointWithValue -> Math.abs(pointWithValue.getLng() - one.getLng()) < 0.06)
                .mapToDouble(PointWithValue::getValue)
                .average()
                .orElse(0d);
    }
}
