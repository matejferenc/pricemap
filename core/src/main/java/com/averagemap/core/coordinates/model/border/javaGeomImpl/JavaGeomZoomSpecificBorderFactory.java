package com.averagemap.core.coordinates.model.border.javaGeomImpl;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorderFactory;
import com.averagemap.core.coordinates.model.border.MultiPolygon;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorder;

public class JavaGeomZoomSpecificBorderFactory implements ZoomSpecificBorderFactory {

    @Override
    public ZoomSpecificBorder createZoomSpecificBorder(MultiPolygon<GoogleMapsPosition> multiPolygon) {
        return new JavaGeomZoomSpecificBorder(multiPolygon);
    }
}
