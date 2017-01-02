package com.averagemap.core.generator;

import com.averagemap.core.average.SimpleSquareAverageCalculator;
import com.averagemap.core.coordinates.DataPoint;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

public class MapGeneratorTest {

    private Set<DataPoint> points;

    @Before
    public void before() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("FLAT_SELL_output.txt").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        points = objectMapper.readValue(file, new TypeReference<HashSet<DataPoint>>() {
        });
    }

    @Test
    public void test1() throws Exception {
        Set<Point<LatLng>> latLngPoints = points.stream()
                .map(point -> new Point<>(new LatLng(point.getLat(), point.getLng()), point.getValue()))
                .collect(toSet());
        Collection<Point<LatLng>> averages = new SimpleSquareAverageCalculator().calculateAverages(latLngPoints);
        assertEquals(averages.size(), points.size());
    }

    @Test
    public void delaunayTriangulationTest() {
        Delaunay_Triangulation dt = new Delaunay_Triangulation();
        points.stream()
                .map(point -> new Point_dt(point.getLat(), point.getLng()))
                .forEach(dt::insertPoint);

        Iterator<Triangle_dt> iterator = dt.trianglesIterator();

        while (iterator.hasNext()) {
            Triangle_dt curr = iterator.next();
            if (!curr.isHalfplane()) {
                System.out.println(curr.p1() + ", " + curr.p2() + ", " + curr.p3());
            }
        }

    }

}
