package com.averagemap.core.duplicate;

import java.util.Collection;
import java.util.stream.Collectors;

import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;

public class AverageResultDuplicateRemover implements DuplicateRemover {

    @Override
    public Collection<Point<LatLng>> removeDuplicates(Collection<Point<LatLng>> points) {
        return points.stream()
                .collect(Collectors.groupingBy(Point::getPosition, Collectors.averagingDouble(Point::getValue)))
                .entrySet().stream()
                .map(entry -> new Point<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
