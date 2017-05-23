package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.Position2D;
import com.averagemap.core.coordinates.model.border.LinearRing;
import com.averagemap.core.coordinates.model.border.MultiPolygon;
import com.averagemap.core.coordinates.model.border.Polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SimpleDuplicateRemover<N extends Number, T extends Position2D<N>> implements DuplicateRemover<N, T> {

    @Override
    public Collection<Point<T>> removeDuplicatePoints(Collection<Point<T>> points) {
        return points.stream()
                .filter(distinctByKey(Point::getPosition))
                .collect(toList());
    }

    @Override
    public List<T> removeDuplicatePositions(Collection<T> positions) {
        return positions.stream()
                .distinct()
                .collect(toList());
    }

    @Override
    public MultiPolygon<T> removeDuplicatePositions(MultiPolygon<T> multiPolygon) {
        return new MultiPolygon<T>(multiPolygon.getPolygons().stream()
                .map(polygon -> new Polygon<T>(removeDuplicatePositions(polygon.getExteriorRing()),
                        polygon.getHoles().stream()
                                .map(this::removeDuplicatePositions)
                                .collect(toList())))
                .collect(toList()));
    }

    private LinearRing<T> removeDuplicatePositions(LinearRing<T> ring) {
        return new LinearRing<T>(ring.getLineString().stream()
                .distinct()
                .collect(toList()));
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}