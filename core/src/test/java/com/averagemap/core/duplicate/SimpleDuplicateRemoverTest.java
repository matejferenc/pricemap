package com.averagemap.core.duplicate;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import com.averagemap.core.coordinates.PointWithValue;

public class SimpleDuplicateRemoverTest {

    @Test
    public void test1() {
        Collection<PointWithValue> points = createPoints();
        Collection<PointWithValue> withoutDuplicates = new SimpleDuplicateRemover().removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
        withoutDuplicates = new SimpleDuplicateRemover().removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
    }

    private Collection<PointWithValue> createPoints() {
        ArrayList<PointWithValue> pointWithValues = new ArrayList<>();
        pointWithValues.add(new PointWithValue(50, 14, 100));
        pointWithValues.add(new PointWithValue(50, 14, 101));
        pointWithValues.add(new PointWithValue(49, 14, 99));
        pointWithValues.add(new PointWithValue(49, 14, 102));
        pointWithValues.add(new PointWithValue(49, 13, 98));
        return pointWithValues;
    }

}
