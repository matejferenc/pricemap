package com.averagemap.core.duplicate;

import com.averagemap.core.coordinates.model.Point;
import com.averagemap.core.coordinates.model.Position2D;
import com.averagemap.core.coordinates.model.border.MultiPolygon;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.*;

public class AverageResultDuplicateRemover<N extends Number, T extends Position2D<N>> implements DuplicateRemover<N, T> {

    @Override
    public Collection<Point<T>> removeDuplicatePoints(Collection<Point<T>> points) {
        return points.stream()
                .collect(groupingBy(Point::getPosition, averagingDouble(Point::getValue)))
                .entrySet().stream()
                .map(entry -> new Point<>(entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    @Override
    public List<T> removeDuplicatePositions(Collection<T> positions) {
        throw new IllegalStateException("not suited for removing duplicate positions");
    }

    @Override
    public MultiPolygon<T> removeDuplicatePositions(MultiPolygon<T> multiPolygon) {
        throw new IllegalStateException("not suited for removing duplicate positions");
    }

}
