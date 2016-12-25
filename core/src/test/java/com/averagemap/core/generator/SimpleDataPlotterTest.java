package com.averagemap.core.generator;

import com.averagemap.core.CoordinatesUtilsTest;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.images.ImageTilesForEveryZoom;
import com.averagemap.core.images.ImageTilesForOneZoom;
import com.averagemap.core.images.ImageTilesSaver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SimpleDataPlotterTest {

    @Test
    public void test1() {
        ZoomSpecificDataPlotter zoomSpecificDataPlotter = new SimpleZoomSpecificDataPlotter();
        SimpleDataPlotter simpleDataPlotter = new SimpleDataPlotter(zoomSpecificDataPlotter);
        List<LatLng> outline = loadCzechRepublicBorder();
        ImageTilesForEveryZoom imageTilesForEveryZoom = simpleDataPlotter.plot(randomPoints(), outline);
        new ImageTilesSaver().saveTiles(imageTilesForEveryZoom, new File("C:\\dev\\java\\tmp"));
        List<ImageTilesForOneZoom> oneZoomTilesList = imageTilesForEveryZoom.getOneZoomTilesList();
    }

    private Collection<Point<LatLng>> randomPoints() {
        List<Point<LatLng>> points = new ArrayList<>();
        points.add(new Point<>(CoordinatesUtilsTest.BRNO, 100));
        points.add(new Point<>(CoordinatesUtilsTest.HODONIN, 101));
        points.add(new Point<>(CoordinatesUtilsTest.QUADRIO, 102));
        return points;
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
