package com.averagemap.core.coordinates;

public class GoogleMapsPosition implements Position2D<Integer> {

    private final int zoom;

    private final int x;

    private final int y;

    public GoogleMapsPosition(int x, int y, int zoom) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleMapsPosition that = (GoogleMapsPosition) o;

        if (zoom != that.zoom) return false;
        if (x != that.x) return false;
        return y == that.y;

    }

    @Override
    public int hashCode() {
        int result = zoom;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "GoogleMapsPosition{" +
                "zoom=" + zoom +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
