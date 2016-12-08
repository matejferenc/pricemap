package com.averagemap.core.duplicate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.averagemap.core.coordinates.Point;

public class SimpleDuplicateRemover implements DuplicateRemover {

    @Override
    public Collection<Point> removeDuplicates(Collection<Point> points) {
        return points.stream()
                .filter(distinctByKey(Point::getLatLng))
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
