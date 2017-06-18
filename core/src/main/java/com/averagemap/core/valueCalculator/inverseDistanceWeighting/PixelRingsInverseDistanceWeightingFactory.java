package com.averagemap.core.valueCalculator.inverseDistanceWeighting;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.distance.Distance;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.valueCalculator.FastPriorityQueue;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import com.averagemap.core.valueCalculator.PointValueCalculatorFactory;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;

public class PixelRingsInverseDistanceWeightingFactory implements PointValueCalculatorFactory {

    private final Distance distance;

    private Map<GoogleMapsPosition, Point<GoogleMapsPosition>> grid;

    private Map<GoogleMapsTile, List<Point<GoogleMapsPosition>>> tileGrid;

    public PixelRingsInverseDistanceWeightingFactory(Distance distance) {
        this.distance = distance;
    }

    @Override
    public void setUp(Collection<Point<GoogleMapsPosition>> points) {
        grid = new HashMap<>();
        points.forEach(point -> {
            GoogleMapsPosition googleMapsPosition = point.getPosition();
            if (grid.get(googleMapsPosition) != null) {
                throw new IllegalStateException("Point already exists on position " + googleMapsPosition);
            } else {
                grid.put(googleMapsPosition, point);
            }
        });
        tileGrid = new HashMap<>();
        points.forEach(point -> {
            GoogleMapsTile googleMapsTile = CoordinatesUtils.positionToTile(point.getPosition());
            List<Point<GoogleMapsPosition>> pointsInTile = tileGrid.computeIfAbsent(googleMapsTile, k1 -> new ArrayList<>());
            pointsInTile.add(point);
        });
    }

    /**
     * Creates {@link PointValueCalculator} for single {@link GoogleMapsTile}
     * @param tile
     * @return
     */
    @Override
    public PointValueCalculator create(GoogleMapsTile tile) {
        Set<Point<GoogleMapsPosition>> filteredPoints = new HashSet<>();

        IntStream.range(0, TILE_SIZE)
                .forEach(i -> {
                            GoogleMapsPosition pixelPositionTop = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(tile, pixelPositionTop));
                            GoogleMapsPosition pixelPositionBottom = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE + TILE_SIZE - 1, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(tile, pixelPositionBottom));
                        }
                );
        IntStream.range(0, TILE_SIZE)
                .forEach(j -> {
                            GoogleMapsPosition pixelPositionLeft = new GoogleMapsPosition(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE + j, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(tile, pixelPositionLeft));
                            GoogleMapsPosition pixelPositionRight = new GoogleMapsPosition(tile.getX() * TILE_SIZE + TILE_SIZE - 1, tile.getY() * TILE_SIZE + j, tile.getZoom());
                            filteredPoints.addAll(getClosestPoints(tile, pixelPositionRight));
                        }
                );

        List<Point<GoogleMapsPosition>> currentTilePoints = tileGrid.get(tile);
        if (currentTilePoints != null) {
            filteredPoints.addAll(currentTilePoints);
        }

        return new PixelRingsInverseDistanceWeighting(distance, filteredPoints);
    }

    private Set<Point<GoogleMapsPosition>> getClosestPoints(GoogleMapsTile tile, GoogleMapsPosition pixelPosition) {
        FastPriorityQueue<Pair<Double, Point<GoogleMapsPosition>>> closestPoints = new FastPriorityQueue<>(
                (Pair<Double, Point<GoogleMapsPosition>> o1, Pair<Double, Point<GoogleMapsPosition>> o2) -> o2.getKey().compareTo(o1.getKey())
        );

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