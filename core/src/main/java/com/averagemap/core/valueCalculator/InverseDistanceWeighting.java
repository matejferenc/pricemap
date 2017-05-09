package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import javafx.util.Pair;

import java.util.Collection;
import java.util.PriorityQueue;

public class InverseDistanceWeighting implements PointValueCalculator {

    private final Distance distance;

    private final Collection<Point<GoogleMapsPosition>> points;

    public InverseDistanceWeighting(Distance distance, Collection<Point<GoogleMapsPosition>> points) {
        this.distance = distance;
        this.points = points;
    }

    @Override
    public double calculate(GoogleMapsPosition pixelPosition) {
        for (Point<GoogleMapsPosition> point : points) {
            if (point.getPosition().getX().equals(pixelPosition.getX())
                    && point.getPosition().getY().equals(pixelPosition.getY())) {
                return point.getValue();
            }
        }

        PriorityQueue<Pair<Double, Double>> closestPoints = new PriorityQueue<>((Pair<Double, Double> o1, Pair<Double, Double> o2) -> o2.getKey().compareTo(o1.getKey()));
        final int k = 10;
        points.forEach(point -> {
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (closestPoints.size() >= k) {
                if (closestPoints.peek().getKey() > distance) {
                    closestPoints.poll();
                    closestPoints.add(new Pair<>(distance, point.getValue()));
                }
            } else {
                closestPoints.add(new Pair<>(distance, point.getValue()));
            }
        });
        double sumOfWeights = closestPoints.stream().mapToDouble(pair -> 1 / pair.getKey()).sum();
        return closestPoints.stream()
                .mapToDouble(pair -> (1 / pair.getKey()) * pair.getValue())
                .sum() / sumOfWeights;
    }

}
