package com.averagemap.core.generator;

import com.averagemap.core.CoordinatesUtilsTest;
import com.averagemap.core.coordinates.DataPoint;
import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.distance.EuclidDistance;
import com.averagemap.core.coordinates.distance.NewYorkDistance;
import com.averagemap.core.duplicate.AverageResultDuplicateRemover;
import com.averagemap.core.duplicate.DuplicateRemover;
import com.averagemap.core.duplicate.SimpleDuplicateRemover;
import com.averagemap.core.images.ImageTilesForEveryZoom;
import com.averagemap.core.images.ImageTilesForOneZoom;
import com.averagemap.core.images.ImageTileSaver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toSet;

public class SimpleDataPlotterTest {

    @Test
    public void test1() throws IOException {
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePointRemover = new AverageResultDuplicateRemover<>();
        DuplicateRemover<Integer, GoogleMapsPosition> duplicatePositionRemover = new SimpleDuplicateRemover<>();
        ImageTileSaver imageTileSaver = new ImageTileSaver(new File("C:\\dev\\java\\tmp"));
//        Distance distance = new NewYorkDistance();
        Distance distance = new EuclidDistance();
        SingleZoomDataPlotter zoomSpecificDataPlotter = new SingleZoomDataPlotterImpl(imageTileSaver, distance);
        int maxZoom = 8;
        SimpleDataPlotter simpleDataPlotter = new SimpleDataPlotter(zoomSpecificDataPlotter, duplicatePointRemover, duplicatePositionRemover, maxZoom);
        List<LatLng> outline = loadCzechRepublicBorder();
        simpleDataPlotter.plot(loadData(), outline);
    }

    private Collection<Point<LatLng>> randomPoints() {
        List<Point<LatLng>> points = new ArrayList<>();
        points.add(new Point<>(CoordinatesUtilsTest.BRNO, 100));
        points.add(new Point<>(CoordinatesUtilsTest.HODONIN, 101));
        points.add(new Point<>(CoordinatesUtilsTest.QUADRIO, 102));
        return points;
    }

    private Set<Point<LatLng>> loadData() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("FLAT_SELL_output.txt").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        Set<DataPoint> points = objectMapper.readValue(file, new TypeReference<HashSet<DataPoint>>() {
        });
        return points.stream()
                .filter(point -> point.getValue() > 1)
                .filter(point -> point.getValue() < 150000)
                .map(point -> new Point<>(new LatLng(point.getLat(), point.getLng()), point.getValue()))
                .collect(toSet());
    }

    private List<LatLng> loadCzechRepublicBorder() {
        List<LatLng> result = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("cz_coords.kml").getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lngLat = line.split(",");
                result.add(new LatLng(Double.parseDouble(lngLat[1]), Double.parseDouble(lngLat[0])));
            }
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
