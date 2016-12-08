package com.averagemap.core.generator;

import com.averagemap.core.coordinates.Area;
import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.Point;

import java.util.Collection;

public interface ZoomSpecificDataPlotter {

    void plot(Collection<Point<GoogleMapsPosition>> points, Area<GoogleMapsPosition> area);

}
