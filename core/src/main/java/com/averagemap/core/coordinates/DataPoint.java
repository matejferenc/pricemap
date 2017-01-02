package com.averagemap.core.coordinates;

public class DataPoint {

    private double value;

    private double lat;

    private double lng;

    public DataPoint(double value, double lat, double lng) {
        this.value = value;
        this.lat = lat;
        this.lng = lng;
    }

    public DataPoint() {
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
