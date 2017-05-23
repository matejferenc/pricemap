package com.averagemap.core.generator.plotter;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;

import java.util.Collection;
import java.util.List;

public interface SingleZoomDataPlotter {

    void plot(Collection<Point<GoogleMapsPosition>> points, List<GoogleMapsPosition> outline, int zoom);

}
