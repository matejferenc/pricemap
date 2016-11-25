package com.averagemap.core;

import org.junit.Test;
import static org.junit.Assert.*;

import com.averagemap.core.generator.Coordinate;
import com.averagemap.core.generator.CoordinatesUtils;
import com.averagemap.core.generator.PixelCoordinate;

public class CoordinateUtilsTest {

    @Test
    public void test1() {
        int zoom = 8;
        Coordinate coordinate = new Coordinate(50.0815446, 14.4200231);
        PixelCoordinate pixels = new CoordinatesUtils().latLngToPixels(coordinate, zoom);
        assertEquals(35393.0, pixels.getX(), 0.000000001);
        assertEquals(22203.0, pixels.getY(), 0.000000001);
    }
}
