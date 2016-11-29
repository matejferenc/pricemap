package com.averagemap.core.duplicate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.PointWithValue;

public class AverageResultDuplicateRemover implements DuplicateRemover {

    @Override
    public Collection<PointWithValue> removeDuplicates(Collection<PointWithValue> points) {
        return points.stream()
                .collect(Collectors.groupingBy(PointWithValue::getLatLng, Collectors.averagingDouble(PointWithValue::getValue)))
                .entrySet().stream()
                .map(entry -> new PointWithValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
