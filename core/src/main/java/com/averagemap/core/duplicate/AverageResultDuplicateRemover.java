package com.averagemap.core.duplicate;

import java.util.Collection;
import java.util.stream.Collectors;

import com.averagemap.core.coordinates.Point;

public class AverageResultDuplicateRemover implements DuplicateRemover {

    @Override
    public Collection<Point> removeDuplicates(Collection<Point> points) {
        return points.stream()
                .collect(Collectors.groupingBy(Point::getLatLng, Collectors.averagingDouble(Point::getValue)))
                .entrySet().stream()
                .map(entry -> new Point(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
