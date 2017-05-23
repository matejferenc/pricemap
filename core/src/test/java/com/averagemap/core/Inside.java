package com.averagemap.core;

import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;

import java.util.List;

public class Inside {

    public static boolean isInside(LngLatAlt point, MultiPolygon multiPolygon) {
        List<List<List<LngLatAlt>>> polygons = multiPolygon.getCoordinates();
        for (List<List<LngLatAlt>> polygon : polygons) {
            if (isInPolygon(point, polygon)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInPolygon(LngLatAlt point, List<List<LngLatAlt>> polygon) {
        if (polygon.isEmpty()) {
            return false;
        }
        List<LngLatAlt> mainRing = polygon.get(0);
        if (!isInRing(point, mainRing)) {
            return false;
        }
        for (int i = 1; i < polygon.size(); i++) {
            List<LngLatAlt> hole = polygon.get(i);
            if (isInRing(point, hole)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isInRing(LngLatAlt point, List<LngLatAlt> ring) {
        return locatePointInRing(point, ring) != RayCrossingCounter.EXTERIOR;
    }

    private static int locatePointInRing(LngLatAlt point, List<LngLatAlt> ring) {
        return RayCrossingCounter.locatePointInRing(point, ring);
    }
}
