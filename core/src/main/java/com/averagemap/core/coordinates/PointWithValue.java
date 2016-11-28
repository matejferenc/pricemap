package com.averagemap.core.coordinates;

public class PointWithValue {

    private double value;

    private LatLng latLng;

    public PointWithValue(double lat, double lng, double value) {
        this.latLng = new LatLng(lat, lng);
        this.value = value;
    }
    public PointWithValue(LatLng latLng, double value) {
        this.latLng = latLng;
        this.value = value;
    }


    public PointWithValue() {

    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
