package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.distance.Distance;
import javafx.util.Pair;

import java.util.Collection;
import java.util.PriorityQueue;

public class InverseDistanceWeighting implements PointValueCalculator {

    private final Distance distance;

    private final Collection<Point<GoogleMapsPosition>> points;

    public final static int K = 10;

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
        points.forEach(point -> {
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (new Double(distance).equals(Double.NaN)) {
                return;
            }
//            distance *= distance;
            if (closestPoints.size() >= K) {
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
