package com.averagemap.core.coordinates;

public class GoogleMapsPosition extends MercatorPosition {

    private final int zoom;

    public GoogleMapsPosition(int x, int y, int zoom) {
        super(x, y);
        this.zoom = zoom;
    }

    public int getZoom() {
        return zoom;
    }

}
