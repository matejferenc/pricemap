package com.averagemap.core.coordinates;

public class GoogleMapsPosition extends MercatorPosition {

    private int zoom;

    public GoogleMapsPosition() {
    }

    public GoogleMapsPosition(int x, int y, int zoom) {
        super(x, y);
        this.zoom = zoom;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
}
