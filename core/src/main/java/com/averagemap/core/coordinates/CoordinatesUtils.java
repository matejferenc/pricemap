package com.averagemap.core.coordinates;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.TilesArea;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class CoordinatesUtils {

    public static int TILE_SIZE = 256;

    public static GoogleMapsPosition latLngToPosition(LatLng latLng, int zoom) {
        double scale = 1 << zoom;
        double siny = Math.sin(latLng.getLat() * Math.PI / 180);
        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        return new GoogleMapsPosition((int) Math.floor(TILE_SIZE * (0.5 + latLng.getLng() / 360) * scale),
                (int) Math.floor(TILE_SIZE * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)) * scale),
                zoom);
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

    public static GoogleMapsTile positionToTile(GoogleMapsPosition position) {
        return new GoogleMapsTile(position.getX() / TILE_SIZE, position.getY() / TILE_SIZE, position.getZoom());
    }

//    public static Area<LatLng> getEncompassingArea(Collection<LatLng> positions) {
//        if (positions.size() == 0) {
//            throw new IllegalArgumentException("empty");
//        }
//        double left = Double.MAX_VALUE;
//        double top = Double.MIN_VALUE;
//        double right = Double.MIN_VALUE;
//        double bottom = Double.MAX_VALUE;
//        for (LatLng latLng : positions) {
//            double lat = latLng.getLat();
//            if (lat > top) {
//                top = lat;
//            } else if (lat < bottom) {
//                bottom = lat;
//            }
//            double lng = latLng.getLng();
//            if (lng < left) {
//                left = lng;
//            } else if (lng > right) {
//                right = lng;
//            }
//        }
//        return new Area<>(new LatLng(top, left), new LatLng(bottom, right));
//    }

    public static TilesArea getEncompassingArea(Collection<GoogleMapsPosition> positions) {
        final int zoom = assertZoomLevelIsTheSame(positions);
        int left = MAX_VALUE;
        int top = MAX_VALUE;
        int right = MIN_VALUE;
        int bottom = MIN_VALUE;
        for (GoogleMapsPosition googleMapsPosition : positions) {
            int x = googleMapsPosition.getX();
            if (x < left) {
                left = x;
            } else if (x > right) {
                right = x;
            }
            int y = googleMapsPosition.getY();
            if (y < top) {
                top = y;
            } else if (y > bottom) {
                bottom = y;
            }
        }
        return new TilesArea(
                positionToTile(new GoogleMapsPosition(left, top, zoom)),
                positionToTile(new GoogleMapsPosition(right, bottom, zoom)));
    }

    private static int assertZoomLevelIsTheSame(Collection<GoogleMapsPosition> positions) {
        final int zoomOfFirst = positions.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("empty"))
                .getZoom();
        boolean allPointsHaveTheSameZoomLevel = positions.stream()
                .map(GoogleMapsPosition::getZoom)
                .allMatch(zoom -> zoom == zoomOfFirst);
        if (!allPointsHaveTheSameZoomLevel) {
            throw new IllegalArgumentException("zoom levels different");
        }
        return zoomOfFirst;
    }

    public static Area toArea(GoogleMapsTile tile) {
        return new Area(toRectangle2D(tile));
    }

    public static Rectangle2D.Double toRectangle2D(GoogleMapsTile tile) {
        return new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE / 2);
    }

}
