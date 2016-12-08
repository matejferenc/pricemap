package com.averagemap.core.generator;

import java.util.Collection;

import com.averagemap.core.coordinates.Point;

public interface PointsPreprocessor {

    Collection<Point> preprocess(Collection<Point> points);

}
