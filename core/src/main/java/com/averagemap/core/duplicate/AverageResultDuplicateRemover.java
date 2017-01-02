package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.Point;
import com.averagemap.core.coordinates.Position2D;

import java.util.Collection;

import static java.util.stream.Collectors.*;

public class AverageResultDuplicateRemover<N extends Number, T extends Position2D<N>> implements DuplicateRemover<N, T> {

    @Override
    public Collection<Point<T>> removeDuplicates(Collection<Point<T>> points) {
        return points.stream()
                .collect(groupingBy(Point::getPosition, averagingDouble(Point::getValue)))
                .entrySet().stream()
                .map(entry -> new Point<>(entry.getKey(), entry.getValue()))
                .collect(toList());
    }

}
