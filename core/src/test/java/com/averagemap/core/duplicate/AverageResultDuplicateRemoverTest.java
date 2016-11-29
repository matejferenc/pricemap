package com.averagemap.core.duplicate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.PointWithValue;

public class AverageResultDuplicateRemoverTest {

    @Test
    public void test1() {
        AverageResultDuplicateRemover averageResultDuplicateRemover = new AverageResultDuplicateRemover();
        Collection<PointWithValue> points = createPoints();
        Collection<PointWithValue> withoutDuplicates = averageResultDuplicateRemover.removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
        assertAverages(withoutDuplicates);
        withoutDuplicates = averageResultDuplicateRemover.removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
        assertAverages(withoutDuplicates);
    }

    private void assertAverages(Collection<PointWithValue> withoutDuplicates) {
        Map<LatLng, Double> values = new HashMap<>();
        withoutDuplicates.forEach(pointWithValue -> values.put(pointWithValue.getLatLng(), pointWithValue.getValue()));
        assertEquals(values.get(new LatLng(50, 14)), 100.5, 0.0000000001);
        assertEquals(values.get(new LatLng(49, 14)), 102.0, 0.0000000001);
        assertEquals(values.get(new LatLng(49, 13)), 88.0, 0.0000000001);
    }

    private Collection<PointWithValue> createPoints() {
        ArrayList<PointWithValue> pointWithValues = new ArrayList<>();
        pointWithValues.add(new PointWithValue(50, 14, 100));
        pointWithValues.add(new PointWithValue(50, 14, 101));
        pointWithValues.add(new PointWithValue(49, 14, 99));
        pointWithValues.add(new PointWithValue(49, 14, 102));
        pointWithValues.add(new PointWithValue(49, 14, 105));
        pointWithValues.add(new PointWithValue(49, 13, 98));
        return pointWithValues;
    }

}
