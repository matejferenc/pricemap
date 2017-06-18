package com.averagemap.core.generator;

import com.averagemap.core.colorCalculator.AbsoluteValueLevelColorCalculator;
import com.averagemap.core.colorCalculator.ColorCalculator;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.distance.EuclidDistance;
import com.averagemap.core.coordinates.model.DataPoint;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.Border;
import com.averagemap.core.coordinates.model.border.JacksonConverter;
import com.averagemap.core.coordinates.model.border.ZoomSpecificBorderFactory;
import com.averagemap.core.coordinates.model.border.javaGeomImpl.JavaGeomZoomSpecificBorderFactory;
import com.averagemap.core.duplicate.AverageResultDuplicateRemover;
import com.averagemap.core.duplicate.DuplicateRemover;
import com.averagemap.core.generator.plotter.DataPlotterImpl;
import com.averagemap.core.generator.plotter.SingleZoomDataPlotter;
import com.averagemap.core.generator.plotter.SingleZoomDataPlotterImpl;
import com.averagemap.core.images.ImageTileSaver;
import com.averagemap.core.valueCalculator.inverseDistanceWeighting.PixelRingsInverseDistanceWeightingFactory;
import com.averagemap.core.valueCalculator.inverseDistanceWeighting.TileRingsInverseDistanceWeightingFactory;
import com.averagemap.core.valueCalculator.PointValueCalculatorFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.MultiPolygon;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class DataPlotterImplTest {

    @Test
    public void testCzFlatPrices() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance;
//        distance = new NewYorkDistance();
        distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new PixelRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 12;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, maxZoom);
        Border border = loadCzechRepublicBorder();
        dataPlotterImpl.plot(loadData(), border);
    }

    @Test
    public void testFrPopulation() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance;
        distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new TileRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 6;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, maxZoom);
        Border border = loadFrenchBorder();
        dataPlotterImpl.plot(loadFrData(), border);
    }

    @Test
    public void testFrAltitude() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance;
        distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new PixelRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 10;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, maxZoom);
        Border border = loadFrenchBorder();
        dataPlotterImpl.plot(loadFrData2(), border);
    }

    private Set<Point<LatLng>> loadFrData2() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr_altitude.xyz").getFile());
        return Files.lines(file.toPath())
                .map(line -> {
                    String[] parts = line.split(" ");
                    return new Point<>(new LatLng(Double.parseDouble(parts[1]), Double.parseDouble(parts[0])), Double.parseDouble(parts[2]));
                })
                .collect(Collectors.toSet());
    }

    private Set<Point<LatLng>> loadFrData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr_population.txt").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        Set<DataPoint> points = objectMapper.readValue(file, new TypeReference<HashSet<DataPoint>>() {
        });
        return points.stream()
                .map(point -> new Point<>(new LatLng(point.getLat(), point.getLng()), point.getValue()))
                .collect(toSet());
    }

    private Set<Point<LatLng>> loadData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("FLAT_SELL_output.txt").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        Set<DataPoint> points = objectMapper.readValue(file, new TypeReference<HashSet<DataPoint>>() {
        });
        return points.stream()
                .filter(point -> point.getValue() > 1000)
                .filter(point -> point.getValue() < 150000)
                .map(point -> new Point<>(new LatLng(point.getLat(), point.getLng()), point.getValue()))
                .collect(toSet());
    }

    private Border loadCzechRepublicBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }

    private Border loadFrenchBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }

    @Test
    public void testJak() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.kml").getFile());
        Kml kml = Kml.unmarshal(file);
        kml.getFeature();
        ((MultiGeometry) ((Placemark) ((Folder) ((Document) kml.getFeature()).getFeature().get(0)).getFeature().get(0)).getGeometry()).getGeometry();
    }

    @Test
    public void testGeoJson() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        featureCollection.getFeatures();
    }
}
