package com.averagemap.core.coordinates;

public class Point<T extends Position2D> {

    private final double value;

    private final T position;

    public Point(T position, double value) {
        this.position = position;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public T getPosition() {
        return position;
    }
}