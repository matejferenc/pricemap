package com.averagemap.core.duplicate;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import com.averagemap.core.coordinates.Point;

public class SimpleDuplicateRemoverTest {

    @Test
    public void test1() {
        Collection<Point> points = createPoints();
        Collection<Point> withoutDuplicates = new SimpleDuplicateRemover().removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
        withoutDuplicates = new SimpleDuplicateRemover().removeDuplicates(points);
        assertEquals(3, withoutDuplicates.size());
    }

    private Collection<Point> createPoints() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(50, 14, 100));
        points.add(new Point(50, 14, 101));
        points.add(new Point(49, 14, 99));
        points.add(new Point(49, 14, 102));
        points.add(new Point(49, 13, 98));
        return points;
    }

}
