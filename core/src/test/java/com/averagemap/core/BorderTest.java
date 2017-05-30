package com.averagemap.core;

import com.averagemap.core.coordinates.model.Area;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.border.Border;
import com.averagemap.core.coordinates.model.border.BorderInTile;
import com.averagemap.core.coordinates.model.border.JacksonConverter;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.MultiPolygon;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BorderTest {

    @Test
    public void testStrategyForZoom8() throws IOException {
        Border border = loadCzechRepublicBorder();
        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(8);
        zoomSpecificBorder.prepareForPlotting();

        GoogleMapsTile tile = new GoogleMapsTile(136, 85, 8);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        Assert.assertFalse(borderInTile.isFull(tile));
        Assert.assertTrue(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(137, 85, 8);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        Assert.assertFalse(borderInTile.isFull(tile));
        Assert.assertFalse(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(138, 86, 8);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        Assert.assertTrue(borderInTile.isFull(tile));
        Assert.assertFalse(borderInTile.isEmpty(tile));
    }

    @Test
    public void testStrategyForZoom10() throws IOException {
        Border border = loadCzechRepublicBorder();
        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(10);
        zoomSpecificBorder.prepareForPlotting();

        GoogleMapsTile tile = new GoogleMapsTile(550, 343, 10);
        BorderInTile borderInTile = zoomSpecificBorder.cropToTile(tile);
        Assert.assertFalse(borderInTile.isFull(tile));
        Assert.assertTrue(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(550, 344, 10);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        Assert.assertFalse(borderInTile.isFull(tile));
        Assert.assertFalse(borderInTile.isEmpty(tile));

        tile = new GoogleMapsTile(552, 344, 10);
        borderInTile = zoomSpecificBorder.cropToTile(tile);
        Assert.assertTrue(borderInTile.isFull(tile));
        Assert.assertFalse(borderInTile.isEmpty(tile));
    }

    private Border loadCzechRepublicBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        return new JacksonConverter().convert((MultiPolygon) geometry);
    }
}
