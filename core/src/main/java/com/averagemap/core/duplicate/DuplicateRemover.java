package com.averagemap.core.duplicate;

import java.util.Collection;

import com.averagemap.core.coordinates.PointWithValue;

public interface DuplicateRemover {

    Collection<PointWithValue> removeDuplicates(Collection<PointWithValue> points);

}
