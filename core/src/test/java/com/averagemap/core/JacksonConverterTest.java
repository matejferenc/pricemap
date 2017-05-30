package com.averagemap.core;

import com.averagemap.core.coordinates.model.border.Border;
import com.averagemap.core.coordinates.model.border.JacksonConverter;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorderFactory;
import com.averagemap.core.coordinates.model.border.javaGeomImpl.JavaGeomZoomSpecificBorderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.MultiPolygon;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JacksonConverterTest {

    @Test
    public void testConvertBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        Border border = new JacksonConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
        String a = "a";
    }
}
