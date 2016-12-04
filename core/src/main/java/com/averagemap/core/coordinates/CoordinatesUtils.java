package com.averagemap.core.coordinates;

import java.util.Collection;

public class CoordinatesUtils {

    public static int TILE_SIZE = 256;

    public static MercatorPosition latLngToPixels(LatLng latLng, int zoom) {
        double scale = 1 << zoom;
        double siny = Math.sin(latLng.getLat() * Math.PI / 180);
        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        return new MercatorPosition((int) Math.floor(TILE_SIZE * (0.5 + latLng.getLng() / 360) * scale),
                (int) Math.floor(TILE_SIZE * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)) * scale));
    }

    public static GoogleMapsTile latLngToTile(LatLng latLng, int zoom) {
        double scale = 1 << zoom;
        double siny = Math.sin(latLng.getLat() * Math.PI / 180);
        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        return new GoogleMapsTile((int) Math.floor((0.5 + latLng.getLng() / 360) * scale),
                (int) Math.floor((0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)) * scale),
                zoom);
    }

    public static GoogleMapsTile getLeftTopCornerOfGoogleTile(LatLng latLng, int zoom) {

        int x;
        int y;
//        return new GoogleMapsTile(x, y, zoom);
        return null;
    }

    public static Area<LatLng> getEncompassingArea(Collection<LatLng> positions) {
        if (positions.size() == 0) {
            throw new IllegalArgumentException("empty");
        }
        double left = Double.MAX_VALUE;
        double top = Double.MIN_VALUE;
        double right = Double.MIN_VALUE;
        double bottom = Double.MAX_VALUE;
        for (LatLng latLng : positions) {
            double lat = latLng.getLat();
            if (lat > top ){
                top = lat;
            } else if (lat < bottom) {
                bottom = lat;
            }
            double lng = latLng.getLng();
            if (lng < left) {
                left = lng;
            } else if (lng > right) {
                right = lng;
            }
        }
        return new Area<>(new LatLng(top, left), new LatLng(bottom, right));
    }

}
