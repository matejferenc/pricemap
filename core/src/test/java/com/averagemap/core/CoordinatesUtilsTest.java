package com.averagemap.core;

import com.averagemap.core.coordinates.*;
import org.junit.Before;
import org.junit.Test;

import static com.averagemap.core.coordinates.CoordinatesUtils.getEncompassingArea;
import static com.averagemap.core.coordinates.CoordinatesUtils.latLngToPixels;
import static com.averagemap.core.coordinates.CoordinatesUtils.latLngToTile;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesUtilsTest {

    private static final LatLng QUADRIO = new LatLng(50.0815446, 14.4200231);

    @Test
    public void pointIsAtCorrectMercatorCoordinates() {
        int zoom = 8;
        MercatorPosition pixels = latLngToPixels(QUADRIO, zoom);
        assertEquals(35393.0, pixels.getX(), 0.000000001);
        assertEquals(22203.0, pixels.getY(), 0.000000001);
    }

    @Test
    public void pointIsInCorrectTileWithZoom8() {
        int zoom = 8;
        GoogleMapsTile googleMapsTile = latLngToTile(QUADRIO, zoom);
        assertEquals(138, googleMapsTile.getX());
        assertEquals(86, googleMapsTile.getY());
        assertEquals(8, googleMapsTile.getZoom());
    }

    @Test
    public void pointIsInCorrectTileWithZoom10() {
        int zoom = 10;
        GoogleMapsTile googleMapsTile = latLngToTile(QUADRIO, zoom);
        assertEquals(553, googleMapsTile.getX());
        assertEquals(346, googleMapsTile.getY());
        assertEquals(10, googleMapsTile.getZoom());
    }

    @Test
    public void pointIsInCorrectTileWithZoom14() {
        int zoom = 14;
        GoogleMapsTile googleMapsTile = latLngToTile(QUADRIO, zoom);
        assertEquals(8848, googleMapsTile.getX());
        assertEquals(5550, googleMapsTile.getY());
        assertEquals(14, googleMapsTile.getZoom());
    }

    @Test
    public void encompassingAreaIsCorrectlyCalculated() {
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(50, 14));
        points.add(new LatLng(51, 14));
        points.add(new LatLng(49, 13));
        points.add(new LatLng(50, 17));
        Area<LatLng> encompassingArea = getEncompassingArea(points);
        assertEquals(51, encompassingArea.getTopLeft().getLat(), 0.00000001);
        assertEquals(13, encompassingArea.getTopLeft().getLng(), 0.00000001);
        assertEquals(49, encompassingArea.getBottomRight().getLat(), 0.00000001);
        assertEquals(17, encompassingArea.getBottomRight().getLng(), 0.00000001);
    }
}