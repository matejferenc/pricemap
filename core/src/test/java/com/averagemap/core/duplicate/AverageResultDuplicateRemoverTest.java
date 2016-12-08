package com.averagemap.core.duplicate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;

public class AverageResultDuplicateRemoverTest {

    @Test
    public void test1() {
        AverageResultDuplicateRemover averageResultDuplicateRemover = new AverageResultDuplicateRemover();
        Collection<Point> points = createPoints();
        Collection<Point> withoutDuplicates = averageResultDuplicateRemover.removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
        assertAverages(withoutDuplicates);
        withoutDuplicates = averageResultDuplicateRemover.removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
        assertAverages(withoutDuplicates);
    }

    private void assertAverages(Collection<Point> withoutDuplicates) {
        Map<LatLng, Double> values = new HashMap<>();
        withoutDuplicates.forEach(pointWithValue -> values.put(pointWithValue.getLatLng(), pointWithValue.getValue()));
        assertEquals(values.get(new LatLng(50, 14)), 100.5, 0.0000000001);
        assertEquals(values.get(new LatLng(49, 14)), 102.0, 0.0000000001);
        assertEquals(values.get(new LatLng(49, 13)), 88.0, 0.0000000001);
    }

    private Collection<Point> createPoints() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(50, 14, 100));
        points.add(new Point(50, 14, 101));
        points.add(new Point(49, 14, 99));
        points.add(new Point(49, 14, 102));
        points.add(new Point(49, 14, 105));
        points.add(new Point(49, 13, 98));
        return points;
    }

}
