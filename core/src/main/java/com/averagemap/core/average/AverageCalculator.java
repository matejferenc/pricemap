package com.averagemap.core.average;

import java.util.Collection;

import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;

public interface AverageCalculator {

    <T extends Position2D> Collection<Point<T>> calculateAverages(Collection<Point<T>> points);

}
