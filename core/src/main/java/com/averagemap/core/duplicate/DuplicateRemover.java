package com.averagemap.core.duplicate;

import java.util.Collection;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;

public interface DuplicateRemover {

    Collection<Point<LatLng>> removeDuplicates(Collection<Point<LatLng>> points);

}
