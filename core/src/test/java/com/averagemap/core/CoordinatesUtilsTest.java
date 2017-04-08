package com.averagemap.core;

import com.averagemap.core.coordinates.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.averagemap.core.coordinates.CoordinatesUtils.*;
import static org.junit.Assert.*;

public class CoordinatesUtilsTest {

    public static final LatLng QUADRIO = new LatLng(50.0815446, 14.4200231);
    public static final LatLng BRNO = new LatLng(49.1910153, 16.6074462);
    public static final LatLng HODONIN = new LatLng(48.8555998, 17.1200108);

    @Test
    public void pointIsAtCorrectMercatorCoordinates() {
        int zoom = 8;
        GoogleMapsPosition position = latLngToPosition(QUADRIO, zoom);
        assertEquals(35393.0, position.getX(), 0.000000001);
        assertEquals(22203.0, position.getY(), 0.000000001);
    }

    @Test
    public void pointIsInCorrectTileWithZoom8() {
        int zoom = 8;
        GoogleMapsTile googleMapsTile = latLngToTile(QUADRIO, zoom);
        assertEquals(138, googleMapsTile.getX().intValue());
        assertEquals(86, googleMapsTile.getY().intValue());
        assertEquals(8, googleMapsTile.getZoom());
    }

    @Test
    public void pointIsInCorrectTileWithZoom10() {
        int zoom = 10;
        GoogleMapsTile googleMapsTile = latLngToTile(QUADRIO, zoom);
        assertEquals(553, googleMapsTile.getX().intValue());
        assertEquals(346, googleMapsTile.getY().intValue());
        assertEquals(10, googleMapsTile.getZoom());
    }

    @Test
    public void pointIsInCorrectTileWithZoom14() {
        int zoom = 14;
        GoogleMapsTile googleMapsTile = latLngToTile(QUADRIO, zoom);
        assertEquals(8848, googleMapsTile.getX().intValue());
        assertEquals(5550, googleMapsTile.getY().intValue());
        assertEquals(14, googleMapsTile.getZoom());
    }

    @Test
    public void positionIsInCorrectTileWithZoom8() {
        int zoom = 8;
        GoogleMapsPosition position = latLngToPosition(QUADRIO, zoom);
        GoogleMapsTile googleMapsTile = positionToTile(position);
        assertEquals(138, googleMapsTile.getX().intValue());
        assertEquals(86, googleMapsTile.getY().intValue());
        assertEquals(8, googleMapsTile.getZoom());
    }

//    @Test
//    public void encompassingAreaIsCorrectlyCalculated() {
//        List<LatLng> points = new ArrayList<>();
//        points.add(new LatLng(50, 14));
//        points.add(new LatLng(51, 14));
//        points.add(new LatLng(49, 13));
//        points.add(new LatLng(50, 17));
//        Area<LatLng> encompassingArea = getEncompassingArea(points);
//        assertEquals(51, encompassingArea.getTopLeft().getLat(), 0.00000001);
//        assertEquals(13, encompassingArea.getTopLeft().getLng(), 0.00000001);
//        assertEquals(49, encompassingArea.getBottomRight().getLat(), 0.00000001);
//        assertEquals(17, encompassingArea.getBottomRight().getLng(), 0.00000001);
//    }


    @Test
    public void encompassingAreaIsCorrectlyCalculatedWithZoom8() {
        int zoom = 8;
        List<GoogleMapsPosition> points = new ArrayList<>();
        points.add(latLngToPosition(QUADRIO, zoom));
        points.add(latLngToPosition(BRNO, zoom));
        points.add(latLngToPosition(HODONIN, zoom));
        TilesArea encompassingArea = getEncompassingArea(points);
        GoogleMapsTile topLeft = encompassingArea.getTopLeft();
        assertEquals(138, topLeft.getX().intValue());
        assertEquals(86, topLeft.getY().intValue());
        assertEquals(8, topLeft.getZoom());
        GoogleMapsTile bottomRight = encompassingArea.getBottomRight();
        assertEquals(140, bottomRight.getX().intValue());
        assertEquals(88, bottomRight.getY().intValue());
        assertEquals(8, bottomRight.getZoom());
    }
}
