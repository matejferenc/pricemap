package com.averagemap.core.average;

import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;

import java.util.Collection;

public interface AverageCalculator {

    <T extends Position2D> Collection<Point<T>> calculateAverages(Collection<Point<T>> points);

}
