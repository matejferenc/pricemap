package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;

public interface BorderInTile {
    
    GoogleMapsTile getTile();

    boolean isFull();

    boolean isEmpty();

    boolean contains(GoogleMapsPosition pixelPosition);

}
