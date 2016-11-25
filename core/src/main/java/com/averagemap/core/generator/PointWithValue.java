package com.averagemap.core.generator;

public class PointWithValue extends Coordinate {

    private double value;

    public PointWithValue(double lat, double lng, double value) {
        super(lat, lng);
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

}
