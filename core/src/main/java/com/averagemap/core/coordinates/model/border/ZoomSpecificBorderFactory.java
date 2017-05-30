package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;

public interface ZoomSpecificBorderFactory {

    ZoomSpecificBorder createZoomSpecificBorder(MultiPolygon<GoogleMapsPosition> multiPolygon);

}
