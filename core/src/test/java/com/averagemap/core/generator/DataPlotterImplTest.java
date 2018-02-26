package com.averagemap.core.generator;

import com.averagemap.core.colorCalculator.AbsoluteValueLevelColorCalculator;
import com.averagemap.core.colorCalculator.ColorCalculator;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.distance.EuclidDistance;
import com.averagemap.core.coordinates.model.DataPoint;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.border.*;
import com.averagemap.core.coordinates.model.border.javaGeomImpl.JavaGeomZoomSpecificBorderFactory;
import com.averagemap.core.duplicate.AverageResultDuplicateRemover;
import com.averagemap.core.duplicate.DuplicateRemover;
import com.averagemap.core.generator.plotter.DataPlotterImpl;
import com.averagemap.core.generator.plotter.SingleZoomDataPlotter;
import com.averagemap.core.generator.plotter.SingleZoomDataPlotterImpl;
import com.averagemap.core.images.ImageTileSaver;
import com.averagemap.core.valueCalculator.PointValueCalculatorFactory;
import com.averagemap.core.valueCalculator.inverseDistanceWeighting.PixelRingsInverseDistanceWeightingFactory;
import com.averagemap.core.valueCalculator.inverseDistanceWeighting.TileRingsInverseDistanceWeightingFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.micromata.opengis.kml.v_2_2_0.*;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.MultiPolygon;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
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
        PointValueCalculatorFactory pointValueCalculatorFactory = new TileRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 12;
        int minZoom = 0;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, minZoom, maxZoom);
        Border border = loadCzechRepublicBorder();
        dataPlotterImpl.plot(loadData(), border);
    }

    @Test
    public void  testFrAltitude() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new TileRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 12;
        int minZoom = 9;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, minZoom, maxZoom);
        Border border = loadFrenchBorder();
        dataPlotterImpl.plot(loadFrData(), border);
    }

    @Test
    public void  testCzAltitude() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new PixelRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 12;
        int minZoom = 0;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, minZoom, maxZoom);
        Border border = loadCzechRepublicBorder();
        dataPlotterImpl.plot(loadCzAltitudeData(), border);
    }

    @Test
    public void  testTwEarthquake() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new TileRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 12;
        int minZoom = 0;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, minZoom, maxZoom);
        Border border = loadTaiwanBorder();
        dataPlotterImpl.plot(loadTwData(), border);
    }

    @Test
    public void  testWorldPrecipitation() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File(new File(this.getClass().getResource("/demo/index.html").getPath()).getParent() + "/img"));
        Distance distance = new EuclidDistance();
        PointValueCalculatorFactory pointValueCalculatorFactory = new PixelRingsInverseDistanceWeightingFactory(distance);
        ColorCalculator colorCalculator = new AbsoluteValueLevelColorCalculator();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, pointValueCalculatorFactory, colorCalculator);
        int maxZoom = 5;
        int minZoom = 0;
        DataPlotterImpl dataPlotterImpl = new DataPlotterImpl(zoomSpecificDataPlotter, duplicatePointRemover, minZoom, maxZoom);
        System.out.println("loading border");
        Border border = loadWorldBorder();
        System.out.println("border loaded");
        dataPlotterImpl.plot(loadWorldPrecipitationData(), border);
    }

    private Set<Point<LatLng>> loadWorldPrecipitationData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("world_precipitation.xyz").getFile());
        return Files.lines(file.toPath())
                .map(line -> {
                    String[] parts = line.split(" ");
                    return new Point<>(new LatLng(Double.parseDouble(parts[1]), Double.parseDouble(parts[0])), Double.parseDouble(parts[2]));
                })
                .collect(Collectors.toSet());
    }

    private Set<Point<LatLng>> loadFrData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr_altitude.xyz").getFile());
        return Files.lines(file.toPath())
                .map(line -> {
                    String[] parts = line.split(" ");
                    return new Point<>(new LatLng(Double.parseDouble(parts[1]), Double.parseDouble(parts[0])), Double.parseDouble(parts[2]));
                })
                .collect(Collectors.toSet());
    }

    private Set<Point<LatLng>> loadCzAltitudeData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz_altitude.xyz").getFile());
        return Files.lines(file.toPath())
                .map(line -> {
                    String[] parts = line.split(" ");
                    return new Point<>(new LatLng(Double.parseDouble(parts[1]), Double.parseDouble(parts[0])), Double.parseDouble(parts[2]));
                })
                .collect(Collectors.toSet());
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

    private Set<Point<LatLng>> loadTwData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("tw_earthquake.geojson").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        List<Feature> features = featureCollection.getFeatures();
        return features.stream()
                .map(feature -> {
                    double mag = (double) feature.getProperties().get("mag");
                    return new Point<>(new LatLng(((org.geojson.Point)feature.getGeometry()).getCoordinates().getLatitude(),
                            ((org.geojson.Point)feature.getGeometry()).getCoordinates().getLongitude()),
                            mag);
                })
                .collect(Collectors.toSet());
    }

    private Border loadCzechRepublicBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonBorderConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }

    private Border loadFrenchBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("fr.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonBorderConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }

    private Border loadTaiwanBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("tw.json").getFile());
        FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
        GeoJsonObject geometry = featureCollection.getFeatures().get(0).getGeometry();
        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        return new JacksonBorderConverter(zoomSpecificBorderFactory).convert((MultiPolygon) geometry);
    }

    private Border loadWorldBorder() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("world.json").getFile());
        return new JacksonBorderReader(new JavaGeomZoomSpecificBorderFactory()).read(file);
    }

    @Test
    public void hugeJson2() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("world.json").getFile());
        long start = System.currentTimeMillis();
        Border border = new JacksonBorderReader(new JavaGeomZoomSpecificBorderFactory()).read(file);
        long end = System.currentTimeMillis();
        System.out.println("Reading border took " + (end - start) / 1000 + " seconds");

        start = System.currentTimeMillis();
        ZoomSpecificBorder zoomSpecificBorder = border.createForZoom(0);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 0 took " + (end - start) / 1000 + " seconds");

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(1);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 1 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(2);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 2 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(3);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 3 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(4);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 4 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(5);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 5 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(6);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 6 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;

        start = System.currentTimeMillis();
        zoomSpecificBorder = border.createForZoom(7);
        end = System.currentTimeMillis();
        System.out.println("Creating border for zoom 7 took " + (end - start) / 1000 + " seconds");
        zoomSpecificBorder = null;
    }

    @Test
    public void hugeJson() throws IOException {
        MultiPolygon result = new MultiPolygon();
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("world.json").getFile());

        JsonFactory f = new MappingJsonFactory();
        JsonParser parser = f.createParser(file);

        JsonToken currentToken;

        currentToken = parser.nextToken();
        if (currentToken != JsonToken.START_OBJECT) {
            System.out.println("Error: root should be object: quiting.");
            throw new IllegalArgumentException("asdf");
        }

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            currentToken = parser.nextToken();
            if (fieldName.equals("features")) {
                int featureIndex = 0;
                if (currentToken == JsonToken.START_ARRAY) {
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        featureIndex++;
                        TreeNode treeNode = parser.readValueAsTree();
                        Feature feature = objectMapper.treeToValue(treeNode, Feature.class);

                        GeoJsonObject geometry = feature.getGeometry();
                        MultiPolygon multiPolygon = (MultiPolygon) geometry;
                        multiPolygon.getCoordinates().forEach(result::add);

                        if (featureIndex % 1000 == 0) {
                            System.out.println("feature " + featureIndex);
                        }
                    }
                } else {
                    System.out.println("Error: records should be an array: skipping.");
                    throw new IllegalArgumentException("1324");
//                    parser.skipChildren();
                }
            } else {
                System.out.println("Unprocessed property: " + fieldName);
                parser.skipChildren();
            }
        }

        parser.close();

        ZoomSpecificBorderFactory zoomSpecificBorderFactory = new JavaGeomZoomSpecificBorderFactory();
        new JacksonBorderConverter(zoomSpecificBorderFactory).convert(result);
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
