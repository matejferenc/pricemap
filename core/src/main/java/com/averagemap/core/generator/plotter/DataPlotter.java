package com.averagemap.core.generator.plotter;

import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.MultiPolygon;

import java.util.Collection;
import java.util.List;

public interface DataPlotter {

    void plot(Collection<Point<LatLng>> points, MultiPolygon<LatLng> border);

}
