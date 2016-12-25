package com.averagemap.core.generator;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.averagemap.core.coordinates.LatLng;
import org.junit.Before;
import org.junit.Test;

import com.averagemap.core.average.SimpleSquareAverageCalculator;
import com.averagemap.core.coordinates.Point;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapGeneratorTest {

    private Set<Point<LatLng>> points;

    @Before
    public void before() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("FLAT_SELL_output.txt").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        points = objectMapper.readValue(file, new TypeReference<HashSet<Point>>() {
        });
    }

    @Test
    public void test1() throws Exception {
        Collection<Point<LatLng>> averages = new SimpleSquareAverageCalculator().calculateAverages(points);
        assertEquals(averages.size(), points.size());

    }

}
