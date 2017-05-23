package com.averagemap.core.generator.plotter;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.MultiPolygon;

import java.util.Collection;
import java.util.List;

public interface SingleZoomDataPlotter {

    void plot(Collection<Point<GoogleMapsPosition>> points, MultiPolygon<GoogleMapsPosition> border, int zoom);

}
