package com.averagemap.core.valueCalculator.inverseDistanceWeighting;

import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.distance.EuclidDistance;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class InverseDistanceWeightingTest {

    @Test
    public void test() {
        Distance distance = new EuclidDistance();
        Collection<Point<GoogleMapsPosition>> points = new ArrayList<>();
        points.add(new Point<>(new GoogleMapsPosition(6, 0, 0), 6));
        points.add(new Point<>(new GoogleMapsPosition(4, 0, 0), 4));
        points.add(new Point<>(new GoogleMapsPosition(0, 0, 0), 0));
        points.add(new Point<>(new GoogleMapsPosition(1, 0, 0), 1));
        points.add(new Point<>(new GoogleMapsPosition(5, 0, 0), 5));
        points.add(new Point<>(new GoogleMapsPosition(2, 0, 0), 2));
        points.add(new Point<>(new GoogleMapsPosition(3, 0, 0), 3));
        InverseDistanceWeighting inverseDistanceWeighting = new InverseDistanceWeighting(distance, points);
        inverseDistanceWeighting.calculate(new GoogleMapsPosition(7, 0, 0));
    }

    @Test
    public void test2() {
        PriorityQueue<Double> closestPoints = new PriorityQueue<>(5, (Double o1, Double o2) -> o2.compareTo(o1));

        closestPoints.offer(7.0);
        closestPoints.offer(2.0);
        closestPoints.offer(3.0);
        closestPoints.offer(6.0);
        closestPoints.offer(1.0);

        bla(closestPoints,5.0);
        bla(closestPoints,4.0);
//        bla(closestPoints,7.0);
        closestPoints.forEach(System.out::println);
    }

    private void bla(PriorityQueue<Double> closestPoints, double value) {
        System.out.println("PEEK: " + closestPoints.peek());
        System.out.println("VALUE: " + value);
        if (closestPoints.peek() > value) {
            closestPoints.poll();
            closestPoints.offer(value);
        }

    }
}
