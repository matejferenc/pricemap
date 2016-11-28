package com.averagemap.core.average;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import com.averagemap.core.coordinates.PointWithValue;

public class SimpleSquareAverageCalculatorTest {

    @Test
    public void test1() {
        Collection<PointWithValue> points = createPoints();
        Collection<PointWithValue> averages = new SimpleSquareAverageCalculator().calculateAverages(points);
        boolean allPointsHaveAverageValue = averages.stream()
                .allMatch(point -> point.getValue() == 100);
        assertTrue(allPointsHaveAverageValue);
    }

    private Collection<PointWithValue> createPoints() {
        ArrayList<PointWithValue> pointWithValues = new ArrayList<>();
        pointWithValues.add(new PointWithValue(50, 14, 100));
        pointWithValues.add(new PointWithValue(50, 14, 101));
        pointWithValues.add(new PointWithValue(50, 14, 99));
        pointWithValues.add(new PointWithValue(50, 14, 102));
        pointWithValues.add(new PointWithValue(50, 14, 98));
        return pointWithValues;
    }

}
