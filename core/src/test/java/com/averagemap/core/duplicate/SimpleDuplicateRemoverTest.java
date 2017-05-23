package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.model.LatLng;
import com.averagemap.core.coordinates.model.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class SimpleDuplicateRemoverTest {

    @Test
    public void test1() {
        Collection<Point<LatLng>> points = createPoints();
        Collection<Point<LatLng>> withoutDuplicates = new SimpleDuplicateRemover().removeDuplicatePoints(points);
        assertEquals(3, withoutDuplicates.size());
        withoutDuplicates = new SimpleDuplicateRemover().removeDuplicatePoints(points);
        assertEquals(3, withoutDuplicates.size());
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
