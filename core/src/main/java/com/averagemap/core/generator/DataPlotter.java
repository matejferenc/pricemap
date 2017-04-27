package com.averagemap.core.generator;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;

import java.util.Collection;
import java.util.List;

public interface DataPlotter {

    void plot(Collection<Point<LatLng>> points, List<LatLng> outline);

}
