package com.averagemap.core.generator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.images.ImageTilesForOneZoom;

import java.util.Collection;
import java.util.List;

public interface ZoomSpecificDataPlotter {

    ImageTilesForOneZoom plot(Collection<Point<GoogleMapsPosition>> points, List<GoogleMapsPosition> outline, int zoom);

}
