package com.averagemap.core.average;

import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.Position2D;

import java.util.Collection;

public interface AverageCalculator {

    <T extends Position2D> Collection<Point<T>> calculateAverages(Collection<Point<T>> points);

}
