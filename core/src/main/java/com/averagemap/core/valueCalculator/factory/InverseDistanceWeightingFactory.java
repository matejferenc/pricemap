package com.averagemap.core.valueCalculator.factory;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.distance.NewYorkDistance;
import com.averagemap.core.valueCalculator.InverseDistanceWeighting;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import javafx.util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;

public class InverseDistanceWeightingFactory implements PointValueCalculatorFactory {

    private final Distance distance;

    private static final int k = 10;

    public InverseDistanceWeightingFactory(Distance distance) {
        this.distance = distance;
    }

    /**
     * Creates {@link PointValueCalculator} for single {@link GoogleMapsTile}
     * @param tile
     * @param points
     * @return
     */
    @Override
    public PointValueCalculator create(GoogleMapsTile tile, Collection<Point<GoogleMapsPosition>> points) {
        GoogleMapsPosition tileCenter = new GoogleMapsPosition(tile.getX() * TILE_SIZE + TILE_SIZE / 2, tile.getY() * TILE_SIZE + TILE_SIZE / 2, tile.getZoom());
        NewYorkDistance newYorkDistance = new NewYorkDistance();
        double maxDistance = Math.pow(2, tile.getZoom()) * 2;
        // filtering out all the points which are too far away - at zoom 6, 1 tile away (256px). Other zooms accordingly
        Collection<Point<GoogleMapsPosition>> finalPoints = points.stream()
                .filter(point -> newYorkDistance.distance(tileCenter, point.getPosition()) < maxDistance)
                .collect(Collectors.toList());
        Set<Point<GoogleMapsPosition>> filteredPoints = new HashSet<>();

        IntStream.range(0, TILE_SIZE)
                .forEach(i -> {
                            GoogleMapsPosition pixelPositionTop = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(finalPoints, pixelPositionTop));
                            GoogleMapsPosition pixelPositionBottom = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE + TILE_SIZE - 1, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(finalPoints, pixelPositionBottom));
                        }
                );
        IntStream.range(0, TILE_SIZE)
                .forEach(j -> {
                            GoogleMapsPosition pixelPositionLeft = new GoogleMapsPosition(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE + j, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(finalPoints, pixelPositionLeft));
                            GoogleMapsPosition pixelPositionRight = new GoogleMapsPosition(tile.getX() * TILE_SIZE + TILE_SIZE - 1, tile.getY() * TILE_SIZE + j, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(finalPoints, pixelPositionRight));
                        }
                );

        Set<Point<GoogleMapsPosition>> inside = finalPoints.stream()
                .filter(point -> point.getPosition().getX() >= tile.getX() * TILE_SIZE)
                .filter(point -> point.getPosition().getX() <= tile.getX() * TILE_SIZE + TILE_SIZE - 1)
                .filter(point -> point.getPosition().getY() >= tile.getY() * TILE_SIZE)
                .filter(point -> point.getPosition().getY() <= tile.getY() * TILE_SIZE + TILE_SIZE - 1)
                .collect(Collectors.toSet());
        filteredPoints.addAll(inside);
//        System.out.println("tile contains " + inside.size() + " points");

        return new InverseDistanceWeighting(distance, filteredPoints);
    }

    private Set<Point<GoogleMapsPosition>> getClosestPoints(Collection<Point<GoogleMapsPosition>> points, GoogleMapsPosition pixelPosition) {
        PriorityQueue<Pair<Double, Point<GoogleMapsPosition>>> closestPoints = new PriorityQueue<>(
                (Pair<Double, Point<GoogleMapsPosition>> o1, Pair<Double, Point<GoogleMapsPosition>> o2) -> o2.getKey().compareTo(o1.getKey())
        );
        points.forEach(point -> {
            double distance = this.distance.distance(point.getPosition(), pixelPosition);
            if (new Double(distance).equals(Double.NaN)) {
                //check for possible NaN values. We skip this point
                return;
            }
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