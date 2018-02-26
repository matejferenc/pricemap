package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.LatLng;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.LngLatAlt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JacksonBorderReader {

    private final ZoomSpecificBorderFactory zoomSpecificBorderFactory;

    private final List<Polygon<LatLng>> polygons = new ArrayList<>();

    public JacksonBorderReader(ZoomSpecificBorderFactory zoomSpecificBorderFactory) {
        this.zoomSpecificBorderFactory = zoomSpecificBorderFactory;
    }

    public Border read(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory f = new MappingJsonFactory();
        JsonParser parser = f.createParser(file);
        JsonToken currentToken = parser.nextToken();
        if (currentToken != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Root should be object");
        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            currentToken = parser.nextToken();
            if (fieldName.equals("features")) {
                if (currentToken == JsonToken.START_ARRAY) {
                    int featureIndex = 0;
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        featureIndex++;
                        Feature feature = objectMapper.treeToValue(parser.readValueAsTree(), Feature.class);
                        if (feature.getGeometry() instanceof org.geojson.MultiPolygon) {
                            org.geojson.MultiPolygon geoJsonMultiPolygon = (org.geojson.MultiPolygon) feature.getGeometry();
                            geoJsonMultiPolygon.getCoordinates().forEach(geoJsonPolygon -> polygons.add(convert(geoJsonPolygon)));
                        } else if (feature.getGeometry() instanceof org.geojson.Polygon) {
                            org.geojson.Polygon geoJsonPolygon = (org.geojson.Polygon) feature.getGeometry();
                            polygons.add(convert(geoJsonPolygon.getCoordinates()));
                        } else {
                            throw new IllegalStateException("Polygon or MultiPolygon expected, but got " + feature.getGeometry().getClass().getSimpleName());
                        }

                        if (featureIndex % 1000 == 0) {
                            System.out.println("feature " + featureIndex);
                        }
                    }
                } else {
                    throw new IllegalStateException("Records should be an array");
                }
            } else {
                System.out.println("Unprocessed property: " + fieldName);
                parser.skipChildren();
            }
        }
        parser.close();
        return new Border(new MultiPolygon<>(polygons), zoomSpecificBorderFactory);
    }

    private Polygon<LatLng> convert(List<List<LngLatAlt>> geoJsonPolygon) {
        List<LatLng> exteriorRingLineString = new ArrayList<>();
        LinearRing<LatLng> exteriorRing = new LinearRing<>(exteriorRingLineString);
        List<LinearRing<LatLng>> holes = new ArrayList<>();
        Polygon<LatLng> polygon = new Polygon<>(exteriorRing, holes);
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
        return polygon;
    }

}
