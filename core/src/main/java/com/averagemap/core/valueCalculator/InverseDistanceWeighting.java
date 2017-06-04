package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

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



        PriorityQueue<Pair<Double, Double>> oldClosestPoints = new PriorityQueue<>((Pair<Double, Double> o1, Pair<Double, Double> o2) -> o2.getKey().compareTo(o1.getKey()));
        points.forEach(point -> {
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (new Double(distance).equals(Double.NaN)) {
                return;
            }
            if (oldClosestPoints.size() >= K) {
                if (oldClosestPoints.peek().getKey() > distance) {
                    oldClosestPoints.poll();
                    oldClosestPoints.add(new Pair<>(distance, point.getValue()));
                }
            } else {
                oldClosestPoints.add(new Pair<>(distance, point.getValue()));
            }
        });

        closestPoints.stream()
                .sorted((Pair<Double, Double> o1, Pair<Double, Double> o2) -> o2.getKey().compareTo(o1.getKey()))
                .forEach(pair -> System.out.print(pair.getKey() + ", "));
        System.out.println();
        oldClosestPoints.stream()
                .sorted((Pair<Double, Double> o1, Pair<Double, Double> o2) -> o2.getKey().compareTo(o1.getKey()))
                .forEach(pair -> System.out.print(pair.getKey() + ", "));
        System.out.println();
        System.out.println("-------------------");


        double sumOfWeights = oldClosestPoints.stream().mapToDouble(pair -> 1 / pair.getKey()).sum();
        return oldClosestPoints.stream()
                .mapToDouble(pair -> (1 / pair.getKey()) * pair.getValue())
                .sum() / sumOfWeights;
    }

}
