package com.averagemap.core.coordinates.model;

public class Area<T extends Position2D> {

    private T topLeft;

    private T bottomRight;

    public Area(T topLeft, T bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public T getTopLeft() {
        return topLeft;
    }

    public T getBottomRight() {
        return bottomRight;
    }

    public int getWidth() {
        return bottomRight.getX().intValue() - topLeft.getX().intValue() + 1;
    }

    public int getHeight() {
        return bottomRight.getY().intValue() - topLeft.getY().intValue() + 1;
    }

}
