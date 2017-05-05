package com.averagemap.core.valueCalculator;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;

public class InverseDistanceWeighting implements PointValueCalculator {

    private final Distance distance;

    private Collection<Point<GoogleMapsPosition>> points;

    public InverseDistanceWeighting(Distance distance) {
        this.distance = distance;
    }

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

    @Override
    public PointValueCalculator prepareForTile(GoogleMapsTile tile, final Collection<Point<GoogleMapsPosition>> points) {
        Set<Point<GoogleMapsPosition>> filteredPoints = new HashSet<>();

        IntStream.range(0, TILE_SIZE)
                .forEach(i -> {
                            GoogleMapsPosition pixelPositionTop = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(points, pixelPositionTop));
                            GoogleMapsPosition pixelPositionBottom = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE + TILE_SIZE - 1, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(points, pixelPositionBottom));
                        }
                );
        IntStream.range(0, TILE_SIZE)
                .forEach(j -> {
                            GoogleMapsPosition pixelPositionLeft = new GoogleMapsPosition(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE + j, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(points, pixelPositionLeft));
                            GoogleMapsPosition pixelPositionRight = new GoogleMapsPosition(tile.getX() * TILE_SIZE + TILE_SIZE - 1, tile.getY() * TILE_SIZE + j, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(points, pixelPositionRight));
                        }
                );

        Set<Point<GoogleMapsPosition>> inside = points.stream()
                .filter(point -> point.getPosition().getX() >= tile.getX() * TILE_SIZE)
                .filter(point -> point.getPosition().getX() <= tile.getX() * TILE_SIZE + TILE_SIZE - 1)
                .filter(point -> point.getPosition().getY() >= tile.getY() * TILE_SIZE)
                .filter(point -> point.getPosition().getY() <= tile.getX() * TILE_SIZE + TILE_SIZE - 1)
                .collect(Collectors.toSet());
        filteredPoints.addAll(inside);
//        System.out.println("tile contains " + inside.size() + " points");

        return new InverseDistanceWeighting(distance, filteredPoints);
    }

    private Set<Point<GoogleMapsPosition>> getClosestPoints(Collection<Point<GoogleMapsPosition>> points, GoogleMapsPosition pixelPosition) {
        PriorityQueue<Pair<Double, Point<GoogleMapsPosition>>> closestPoints = new PriorityQueue<>(
                (Pair<Double, Point<GoogleMapsPosition>> o1, Pair<Double, Point<GoogleMapsPosition>> o2) -> o2.getKey().compareTo(o1.getKey())
        );
        final int k = 10;
        points.forEach(point -> {
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (closestPoints.size() >= k) {
                if (closestPoints.peek().getKey() > distance) {
                    closestPoints.poll();
                    closestPoints.add(new Pair<>(distance, point));
                }
            } else {
                closestPoints.add(new Pair<>(distance, point));
            }
        });
        return closestPoints.stream()
                .map(Pair::getValue)
                .collect(Collectors.toSet());
    }

}
