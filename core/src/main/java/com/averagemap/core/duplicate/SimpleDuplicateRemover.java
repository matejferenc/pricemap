package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimpleDuplicateRemover<N extends Number, T extends Position2D<N>> implements DuplicateRemover<N, T> {

    @Override
    public Collection<Point<T>> removeDuplicates(Collection<Point<T>> points) {
        return points.stream()
                .filter(distinctByKey(Point::getPosition))
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}