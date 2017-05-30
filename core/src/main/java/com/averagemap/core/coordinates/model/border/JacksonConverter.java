package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JacksonConverter {

    private final ZoomSpecificBorderFactory zoomSpecificBorderFactory;

    public JacksonConverter(ZoomSpecificBorderFactory zoomSpecificBorderFactory) {
        this.zoomSpecificBorderFactory = zoomSpecificBorderFactory;
    }

    public Border convert(org.geojson.MultiPolygon geoJsonMultiPolygon) {
        List<Polygon<LatLng>> polygons = new ArrayList<>();
        geoJsonMultiPolygon.getCoordinates().forEach(geoJsonPolygon -> {
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
