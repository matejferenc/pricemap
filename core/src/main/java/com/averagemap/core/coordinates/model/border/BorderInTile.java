package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;

public interface BorderInTile {

    boolean isFull(GoogleMapsTile tile);

    boolean isEmpty(GoogleMapsTile tile);

    boolean contains(GoogleMapsPosition pixelPosition);

}
