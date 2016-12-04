package com.averagemap.core.coordinates;

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

    public void setTopLeft(T topLeft) {
        this.topLeft = topLeft;
    }

    public T getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(T bottomRight) {
        this.bottomRight = bottomRight;
    }
}
