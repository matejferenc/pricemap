package com.averagemap.core.coordinates;

public class InSquarePosition implements Position2D<Integer> {

    private final int x;

    private final int y;

    public InSquarePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Integer getX() {
        return x;
    }

    @Override
    public Integer getY() {
        return y;
    }
}
