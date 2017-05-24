package com.averagemap.core.generator.plotter;

import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.Border;

import java.util.Collection;

public interface DataPlotter {

    void plot(Collection<Point<LatLng>> points, Border border);

}
