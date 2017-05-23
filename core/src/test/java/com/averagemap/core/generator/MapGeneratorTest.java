package com.averagemap.core.generator;

import com.averagemap.core.average.SimpleSquareAverageCalculator;
import com.averagemap.core.coordinates.model.DataPoint;
import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

}
