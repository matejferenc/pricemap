package com.averagemap.core.generator;

import java.util.Collection;

import com.averagemap.core.coordinates.PointWithValue;

public interface PointsPreprocessor {

    Collection<PointWithValue> preprocess(Collection<PointWithValue> points);

}
