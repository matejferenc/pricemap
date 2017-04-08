package com.averagemap.core.generator;

import java.util.Collection;

import com.averagemap.core.average.AverageCalculator;
import com.averagemap.core.coordinates.LatLng;
import com.averagemap.core.coordinates.Point;
import com.averagemap.core.duplicate.DuplicateRemover;

public class SimplePointsPreprocessor implements PointsPreprocessor {

    private DuplicateRemover duplicateRemover;

    private AverageCalculator averageCalculator;

    public SimplePointsPreprocessor(DuplicateRemover duplicateRemover, AverageCalculator averageCalculator) {
        this.duplicateRemover = duplicateRemover;
        this.averageCalculator = averageCalculator;
    }

    @Override
    public Collection<Point<LatLng>> preprocess(Collection<Point<LatLng>> points) {
        return averageCalculator.calculateAverages(duplicateRemover.removeDuplicatePoints(points));
    }
}
