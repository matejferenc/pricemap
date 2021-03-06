package com.averagemap.core.average;

import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class SimpleSquareAverageCalculatorTest {

    @Test
    public void test1() {
        Collection<Point<LatLng>> points = createPoints();
        Collection<Point<LatLng>> averages = new SimpleSquareAverageCalculator().calculateAverages(points);
        boolean allPointsHaveAverageValue = averages.stream()
                .allMatch(point -> point.getValue() == 100);
        assertTrue(allPointsHaveAverageValue);
    }

    private Collection<Point<LatLng>> createPoints() {
        ArrayList<Point<LatLng>> points = new ArrayList<>();
        points.add(new Point<>(new LatLng(50, 14), 100));
        points.add(new Point<>(new LatLng(50, 14), 101));
        points.add(new Point<>(new LatLng(50, 14), 99));
        points.add(new Point<>(new LatLng(50, 14), 102));
        points.add(new Point<>(new LatLng(50, 14), 98));
        return points;
    }

}
