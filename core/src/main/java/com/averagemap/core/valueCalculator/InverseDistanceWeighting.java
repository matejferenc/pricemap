package com.averagemap.core.valueCalculator;

import com.averagemap.core.FastPriorityQueue;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InverseDistanceWeighting implements PointValueCalculator {

    private final Distance distance;

    private final List<Point<GoogleMapsPosition>> points;

    public final static int K = 10;

    public InverseDistanceWeighting(Distance distance, Collection<Point<GoogleMapsPosition>> points) {
        this.distance = distance;
        this.points = new ArrayList<>(points);
    }

    @Override
    public double calculate(GoogleMapsPosition pixelPosition) {
        for (Point<GoogleMapsPosition> point : points) {
            if (point.getPosition().getX().equals(pixelPosition.getX())
                    && point.getPosition().getY().equals(pixelPosition.getY())) {
                return point.getValue();
            }
        }

        FastPriorityQueue<Pair<Double, Double>> closestPoints = new FastPriorityQueue<>(K + 1, (Pair<Double, Double> o1, Pair<Double, Double> o2) -> o2.getKey().compareTo(o1.getKey()));

        for(int i = 0; i < K; i++) {
            Point<GoogleMapsPosition> point = points.get(i);
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (new Double(distance).equals(Double.NaN)) {
                continue;
            }
            closestPoints.offer(new Pair<>(distance, point.getValue()));
        }

        for(int i = K; i < points.size(); i++) {
            Point<GoogleMapsPosition> point = points.get(i);
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (new Double(distance).equals(Double.NaN)) {
                continue;
            }
            if (closestPoints.peek().getKey() > distance) {
                closestPoints.poll();
                closestPoints.offer(new Pair<>(distance, point.getValue()));
            }
        }

        double sumOfWeights = closestPoints.stream().mapToDouble(pair -> 1 / pair.getKey()).sum();
        return closestPoints.stream()
                .mapToDouble(pair -> (1 / pair.getKey()) * pair.getValue())
                .sum() / sumOfWeights;
    }

}
