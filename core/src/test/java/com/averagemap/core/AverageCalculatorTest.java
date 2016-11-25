package com.averagemap.core;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.averagemap.core.generator.AverageCalculator;
import com.averagemap.core.generator.PointWithValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AverageCalculatorTest {

    private Set<PointWithValue> pointWithValues;

    @Before
    public void before() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("FLAT_SELL_output.txt").getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        pointWithValues = objectMapper.readValue(file, new TypeReference<HashSet<PointWithValue>>() {});
    }

    @Test
    public void test1() throws Exception {
        Collection<PointWithValue> averages = new AverageCalculator().calculateAverages(pointWithValues);
        assertEquals(averages.size(), pointWithValues.size());

    }

}
