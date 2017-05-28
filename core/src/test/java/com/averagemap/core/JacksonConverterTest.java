package com.averagemap.core;

import com.averagemap.core.coordinates.model.border.Border;
import com.averagemap.core.coordinates.model.border.JacksonConverter;
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
        Border border = new JacksonConverter().convert((MultiPolygon) geometry);
        String a = "a";
    }
}
