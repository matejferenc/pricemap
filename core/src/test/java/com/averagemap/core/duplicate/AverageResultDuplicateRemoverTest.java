package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AverageResultDuplicateRemoverTest {

    @Test
    public void test1() {
        AverageResultDuplicateRemover<Double, LatLng> averageResultDuplicateRemover = new AverageResultDuplicateRemover<>();
        Collection<Point<LatLng>> points = createPoints();
        Collection<Point<LatLng>> withoutDuplicates = averageResultDuplicateRemover.removeDuplicatePoints(points);
        assertEquals(3, withoutDuplicates.size());
        assertAverages(withoutDuplicates);
        withoutDuplicates = averageResultDuplicateRemover.removeDuplicatePoints(points);
        assertEquals(3, withoutDuplicates.size());
        assertAverages(withoutDuplicates);
    }

    private void assertAverages(Collection<Point<LatLng>> withoutDuplicates) {
        Map<LatLng, Double> values = new HashMap<>();
        withoutDuplicates.forEach(pointWithValue -> values.put(pointWithValue.getPosition(), pointWithValue.getValue()));
        assertEquals(values.get(new LatLng(50, 14)), 100.5, 0.0000000001);
        assertEquals(values.get(new LatLng(49, 14)), 102.0, 0.0000000001);
        assertEquals(values.get(new LatLng(49, 13)), 88.0, 0.0000000001);
    }

    private Collection<Point<LatLng>> createPoints() {
        ArrayList<Point<LatLng>> points = new ArrayList<>();
        points.add(new Point<>(new LatLng(50, 14), 100));
        points.add(new Point<>(new LatLng(50, 14), 101));
        points.add(new Point<>(new LatLng(49, 14), 99));
        points.add(new Point<>(new LatLng(49, 14), 102));
        points.add(new Point<>(new LatLng(49, 14), 105));
        points.add(new Point<>(new LatLng(49, 13), 98));
        return points;
    }

}
