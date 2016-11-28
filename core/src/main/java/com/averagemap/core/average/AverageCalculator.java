package com.averagemap.core.average;

import java.util.Collection;

import com.averagemap.core.coordinates.PointWithValue;

public interface AverageCalculator {

    Collection<PointWithValue> calculateAverages(Collection<PointWithValue> pointWithValues);

}
