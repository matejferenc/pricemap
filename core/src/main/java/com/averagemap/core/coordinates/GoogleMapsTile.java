package com.averagemap.core.coordinates;

public class GoogleMapsTile implements Position2D<Integer> {

    private final int zoom;

    private final int x;

    private final int y;

    public GoogleMapsTile(int x, int y, int zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public int getZoom() {
        return zoom;
    }

    @Override
    public Integer getX() {
        return x;
    }

    @Override
    public Integer getY() {
        return y;
    }
}
