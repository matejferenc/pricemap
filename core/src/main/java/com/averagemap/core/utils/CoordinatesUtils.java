package com.averagemap.core.utils;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.MercatorPosition;

public class CoordinatesUtils {

    public static int TILE_SIZE = 256;

    public GoogleMapsPosition latLngToPixels(LatLng latLng, int zoom) {
        double scale = 1 << zoom;
        double siny = Math.sin(latLng.getLat() * Math.PI / 180);
        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        return new GoogleMapsPosition((int) Math.floor(TILE_SIZE * (0.5 + latLng.getLng() / 360) * scale),
                (int) Math.floor(TILE_SIZE * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)) * scale),
                zoom);
    }

}
