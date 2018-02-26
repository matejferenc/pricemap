package com.averagemap.core;

import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.border.*;
import com.averagemap.core.coordinates.model.border.javaGeomImpl.JavaGeomZoomSpecificBorderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.MultiPolygon;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BorderTest {

    @Test
    public void testStrategyForZoom8() throws IOException {
        Border border = loadCzechRepublicBorder();
        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(8);

        GoogleMapsTile tile = new GoogleMapsTile(136, 85, 8);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertTrue(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(137, 85, 8);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertFalse(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(138, 86, 8);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertTrue(borderInTile.isFull(tile));
        assertFalse(borderInTile.isEmpty(tile));
    }

    @Test
    public void testStrategyForZoom10() throws IOException {
        Border border = loadCzechRepublicBorder();
        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(10);

        GoogleMapsTile tile = new GoogleMapsTile(550, 343, 10);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertTrue(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(550, 344, 10);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertFalse(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(552, 344, 10);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertTrue(borderInTile.isFull(tile));
        assertFalse(borderInTile.isEmpty(tile));
    }

    @Test
    public void testStrategyForHoleZoom8() throws IOException {
        Border border = loadFranceBorder();

        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(8);
        GoogleMapsTile tile = new GoogleMapsTile(129, 94, 8);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertFalse(borderInTile.isEmpty(tile));

        zoomSpecificBorder = border.createForZoom(13);
        tile = new GoogleMapsTile(4141, 3027, 13);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertFalse(borderInTile.isEmpty(tile));

        zoomSpecificBorder = border.createForZoom(15);
        tile = new GoogleMapsTile(16563, 12107, 15);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        assertFalse(borderInTile.isFull(tile));
        assertTrue(borderInTile.isEmpty(tile));
    }

    private Border loadCzechRepublicBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonBorderConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }

    private Border loadFranceBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonBorderConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }
}
