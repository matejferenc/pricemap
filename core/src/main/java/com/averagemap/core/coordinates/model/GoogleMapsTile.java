package com.averagemap.core.coordinates.model;

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

    @Override
    public int hashCode() {
        int result = zoom;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleMapsTile that = (GoogleMapsTile) o;

        if (zoom != that.zoom) return false;
        if (x != that.x) return false;
        return y == that.y;
    }
}
