package com.averagemap.core.generator;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;

import java.util.Collection;

public interface PointsPreprocessor {

    Collection<Point<LatLng>> preprocess(Collection<Point<LatLng>> points);

}
