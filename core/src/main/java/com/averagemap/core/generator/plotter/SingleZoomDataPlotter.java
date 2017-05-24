package com.averagemap.core.generator.plotter;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorder;

import java.util.Collection;

public interface SingleZoomDataPlotter {

    void plot(Collection<Point<GoogleMapsPosition>> points, ZoomSpecificBorder border, int zoom);

}
