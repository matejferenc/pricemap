package com.averagemap.core.valueCalculator.inverseDistanceWeighting;

import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.valueCalculator.FastPriorityQueue;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;

public class PixelRingsInverseDistanceWeighting implements PointValueCalculator {

    private final Distance distance;

    private final List<Point<GoogleMapsPosition>> points;

    public final static int K = 5;

    private Map<GoogleMapsPosition, Point<GoogleMapsPosition>> grid;

    public PixelRingsInverseDistanceWeighting(Distance distance, Collection<Point<GoogleMapsPosition>> points) {
        this.distance = distance;
        this.points = new ArrayList<>(points);

        grid = new HashMap<>();
        points.forEach(point -> {
            GoogleMapsPosition googleMapsPosition = point.getPosition();
            if (grid.get(googleMapsPosition) != null) {
                throw new IllegalStateException("Point already exists on position " + googleMapsPosition);
            } else {
                grid.put(googleMapsPosition, point);
            }
        });
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


        final Set<Point<GoogleMapsPosition>> currentRing = new HashSet<>();
        final Set<Point<GoogleMapsPosition>> nextRing = new HashSet<>();
        final Set<Point<GoogleMapsPosition>> temp = new HashSet<>();
        for (int i = 1; i < 10 * 256; i++) {
            int finalI = i;
            temp.clear();
            nextRing.forEach(point -> {
                if (distance.distance(point.getPosition(), pixelPosition) < finalI * TILE_SIZE) {
                    currentRing.add(point);
                } else {
                    temp.add(point);
                }
            });
            nextRing.clear();
            nextRing.addAll(temp);
            temp.clear();
            if (i == 1) {
                temp.addAll(getRing(0, pixelPosition));
            }
            temp.addAll(getRing(i, pixelPosition));
            temp.forEach(point -> {
                if (distance.distance(point.getPosition(), pixelPosition) < finalI * TILE_SIZE) {
                    currentRing.add(point);
                } else {
                    nextRing.add(point);
                }
            });
            if (currentRing.size() >= InverseDistanceWeighting.K) {
                break;
            }
        }

        currentRing.forEach(point -> {
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (new Double(distance).equals(Double.NaN)) {
                //check for possible NaN values. We skip this point
                return;
            }
            if (closestPoints.size() >= InverseDistanceWeighting.K) {
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

    private Collection<? extends Point<GoogleMapsPosition>> getRing(int ring, GoogleMapsPosition googleMapsPosition) {
        Set<Point<GoogleMapsPosition>> result = new HashSet<>();
        if (ring == 0) {
            add(result, grid.get(googleMapsPosition));
            return result;
        } else if (ring == 1) {
            GoogleMapsPosition currentTile = new GoogleMapsPosition(googleMapsPosition.getX() - 1, googleMapsPosition.getY() - 1, googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX() - 1, googleMapsPosition.getY(), googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX() - 1, googleMapsPosition.getY() + 1, googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX(), googleMapsPosition.getY() - 1, googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX(), googleMapsPosition.getY() + 1, googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX() + 1, googleMapsPosition.getY() - 1, googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX() + 1, googleMapsPosition.getY(), googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            currentTile = new GoogleMapsPosition(googleMapsPosition.getX() + 1, googleMapsPosition.getY() + 1, googleMapsPosition.getZoom());
            add(result, grid.get(currentTile));
            return result;
        }
        IntStream.rangeClosed(-ring, ring)
                .forEach(i -> {
                    GoogleMapsPosition currentTile = new GoogleMapsPosition(googleMapsPosition.getX() + i, googleMapsPosition.getY() - ring, googleMapsPosition.getZoom());
                    add(result, grid.get(currentTile));
                    currentTile = new GoogleMapsPosition(googleMapsPosition.getX() + i, googleMapsPosition.getY() + ring, googleMapsPosition.getZoom());
                    add(result, grid.get(currentTile));
                    currentTile = new GoogleMapsPosition(googleMapsPosition.getX() - ring, googleMapsPosition.getY() + i, googleMapsPosition.getZoom());
                    add(result, grid.get(currentTile));
                    currentTile = new GoogleMapsPosition(googleMapsPosition.getX() + ring, googleMapsPosition.getY() + i, googleMapsPosition.getZoom());
                    add(result, grid.get(currentTile));

                });
        return result;
    }

    private void add(Set<Point<GoogleMapsPosition>> result, Point<GoogleMapsPosition> currentPositionPoint) {
        if (currentPositionPoint != null) {
            result.add(currentPositionPoint);
        }
    }

}
