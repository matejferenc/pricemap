package com.averagemap.core.coordinates;

public class MercatorPosition implements Position2D<Integer> {

    private final int x;

    private final int y;

    public MercatorPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

}
