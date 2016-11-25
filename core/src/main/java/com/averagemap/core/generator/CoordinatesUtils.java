package com.averagemap.core.generator;

public class CoordinatesUtils {

    public static int TILE_SIZE = 256;

    /*
        Converts lat/lon to pixel coordinates in given zoom of the EPSG:4326 pyramid
        http://www.maptiler.org/google-maps-coordinates-tile-bounds-projection/
     */
    public PixelCoordinate latLngToPixels(Coordinate coordinate, int zoom) {
        double scale = 1 << zoom;
        PixelCoordinate worldCoordinate = project(coordinate);
        return new PixelCoordinate(Math.floor(worldCoordinate.getX() * scale), Math.floor(worldCoordinate.getY() * scale));
    }

    private PixelCoordinate project(Coordinate latLng) {
        double siny = Math.sin(latLng.getLat() * Math.PI / 180);

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        return new PixelCoordinate(TILE_SIZE * (0.5 + latLng.getLng() / 360), TILE_SIZE * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)));
    }

}
