package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.Position2D;

import java.util.Collection;
import java.util.List;

public interface DuplicateRemover<N extends Number, T extends Position2D<N>> {

    Collection<Point<T>> removeDuplicatePoints(Collection<Point<T>> points);

    List<T> removeDuplicatePositions(Collection<T> positions);

}
