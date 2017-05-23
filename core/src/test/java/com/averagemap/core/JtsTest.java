package com.averagemap.core;

import com.averagemap.core.coordinates.model.Area;
import com.averagemap.core.coordinates.model.LatLng;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class JtsTest {

    @Test
    public void test1() throws IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        LngLatAlt point = new LngLatAlt(1.9238502, 44.6737574, 0);
        boolean inside = Inside.isInside(point, (MultiPolygon) geometry);
        System.out.println("inside: " + inside);

        point = new LngLatAlt(1.9801325, 42.4627851, 0);
        inside = Inside.isInside(point, (MultiPolygon) geometry);
        System.out.println("inside: " + inside);
    }

    @Test
    public void testPerformance() throws IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        Area<LatLng> area = getArea((MultiPolygon) geometry);

        double top = area.getTopLeft().getLat();
        double left = area.getTopLeft().getLng();
        double bottom = area.getBottomRight().getLat();
        double right = area.getBottomRight().getLng();
        double width = right - left;
        double height = top - bottom;

        Random random = new Random(0);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 65536; i ++) {
            double longitude = random.nextDouble() * width + left;
            double latitude = random.nextDouble() * height + bottom;
            LngLatAlt point = new LngLatAlt(longitude, latitude, 0);
            boolean inside = Inside.isInside(point, (MultiPolygon) geometry);
//            System.out.println("inside: " + inside);
        }
        long end = System.currentTimeMillis();
        System.out.println("execution time: " + (end - start) + " milliseconds");
    }

    private Area<LatLng> getArea(MultiPolygon multiPolygon) {
        final double[] left = {Double.MAX_VALUE};
        final double[] right = {Double.MIN_VALUE};
        final double[] top = {Double.MIN_VALUE};
        final double[] bottom = {Double.MAX_VALUE};
        multiPolygon.getCoordinates().forEach(polygon -> {
            polygon.forEach(pointList -> {
                pointList.forEach(point -> {
                    if (point.getLongitude() < left[0]) {
                        left[0] = point.getLongitude();
                    }
                    if (point.getLongitude() > right[0]) {
                        right[0] = point.getLongitude();
                    }
                    if (point.getLatitude() > top[0]) {
                        top[0] = point.getLatitude();
                    }
                    if (point.getLatitude() < bottom[0]) {
                        bottom[0] = point.getLatitude();
                    }
                });
            });
        });
        return new Area<>(new LatLng(top[0], left[0]), new LatLng(bottom[0], right[0]));
    }
}
