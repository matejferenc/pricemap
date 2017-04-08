package com.averagemap.core.coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TilesArea {

    private GoogleMapsTile topLeft;

    private GoogleMapsTile bottomRight;

    public TilesArea(GoogleMapsTile topLeft, GoogleMapsTile bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public GoogleMapsTile getTopLeft() {
        return topLeft;
    }

    public GoogleMapsTile getBottomRight() {
        return bottomRight;
    }

    public int getWidth() {
        return bottomRight.getX().intValue() - topLeft.getX().intValue() + 1;
    }

    public int getHeight() {
        return bottomRight.getY().intValue() - topLeft.getY().intValue() + 1;
    }

    public Stream<GoogleMapsTile> stream() {
        List<GoogleMapsTile> tiles = new ArrayList<>();
        IntStream.rangeClosed(topLeft.getX(), bottomRight.getX())
                .forEach(i -> {
                    IntStream.rangeClosed(topLeft.getY(), bottomRight.getY())
                            .forEach(j -> {
                                tiles.add(new GoogleMapsTile(i, j, topLeft.getZoom()));
                            });
                });
        return tiles.stream();
    }
}
