package com.averagemap.core.coordinates;

public class GoogleMapsTile extends MercatorPosition {

    private int zoom;

    public GoogleMapsTile() {
    }

    public GoogleMapsTile(int x, int y, int zoom) {
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
