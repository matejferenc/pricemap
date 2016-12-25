package com.averagemap.core.generator;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.images.ImageTilesForEveryZoom;

import java.util.Collection;
import java.util.List;

public interface DataPlotter {

    ImageTilesForEveryZoom plot(Collection<Point<LatLng>> points, List<LatLng> outline);

}
