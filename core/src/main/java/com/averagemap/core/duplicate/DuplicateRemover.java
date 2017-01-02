package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;

import java.util.Collection;

public interface DuplicateRemover<N extends Number, T extends Position2D<N>> {

    Collection<Point<T>> removeDuplicates(Collection<Point<T>> points);

}
