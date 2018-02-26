package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JacksonBorderConverter {

    private final ZoomSpecificBorderFactory zoomSpecificBorderFactory;

    public JacksonBorderConverter(ZoomSpecificBorderFactory zoomSpecificBorderFactory) {
        this.zoomSpecificBorderFactory = zoomSpecificBorderFactory;
    }

    public Border convert(org.geojson.MultiPolygon geoJsonMultiPolygon) {
        List<Polygon<LatLng>> polygons = new ArrayList<>();
        final int[] i = {0};
        geoJsonMultiPolygon.getCoordinates().forEach(geoJsonPolygon -> {
            i[0]++;
            if (i[0] % 1000 == 0) {
                System.out.println("polygon " + i[0]);
            }
            List<LatLng> exteriorRingLineString = new ArrayList<>();
            LinearRing<LatLng> exteriorRing = new LinearRing<>(exteriorRingLineString);
            List<LinearRing<LatLng>> holes = new ArrayList<>();
            Polygon<LatLng> polygon = new Polygon<>(exteriorRing, holes);
            polygons.add(polygon);
            geoJsonPolygon.forEach(pointList -> {
                List<LatLng> lineString = pointList.stream()
                        .map(latLngAlt -> new LatLng(latLngAlt.getLatitude(), latLngAlt.getLongitude()))
                        .collect(toList());
                if (pointList == geoJsonPolygon.get(0)) {
                    exteriorRingLineString.addAll(lineString);
                } else {
                    holes.add(new LinearRing<>(lineString));
                }
            });
        });
        MultiPolygon<LatLng> multipolygon = new MultiPolygon<>(polygons);
        return new Border(multipolygon, zoomSpecificBorderFactory);
    }
}
