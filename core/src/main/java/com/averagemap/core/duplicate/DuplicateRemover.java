package com.averagemap.core.duplicate;

import java.util.Collection;

import com.averagemap.core.coordinates.Point;

public interface DuplicateRemover {

    Collection<Point> removeDuplicates(Collection<Point> points);

}
