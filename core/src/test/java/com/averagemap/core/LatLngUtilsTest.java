package com.averagemap.core;

import org.junit.Test;
import static org.junit.Assert.*;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.utils.CoordinatesUtils;
import com.averagemap.core.coordinates.MercatorPosition;

public class LatLngUtilsTest {

    @Test
    public void test1() {
        int zoom = 8;
        LatLng latLng = new LatLng(50.0815446, 14.4200231);
        MercatorPosition pixels = new CoordinatesUtils().latLngToPixels(latLng, zoom);
        assertEquals(35393.0, pixels.getX(), 0.000000001);
        assertEquals(22203.0, pixels.getY(), 0.000000001);
    }
}
