package com.averagemap.core.generator;

import java.util.Collection;

import com.averagemap.core.average.AverageCalculator;
import com.averagemap.core.average.SimpleSquareAverageCalculator;
import com.averagemap.core.coordinates.PointWithValue;
import com.averagemap.core.duplicate.AverageResultDuplicateRemover;
import com.averagemap.core.duplicate.DuplicateRemover;
import com.averagemap.core.duplicate.SimpleDuplicateRemover;

public class MapGenerator {

    public void generateMap(Collection<PointWithValue> points) {
        DuplicateRemover duplicateRemover = new AverageResultDuplicateRemover();
        AverageCalculator averageCalculator = new SimpleSquareAverageCalculator();

        Collection<PointWithValue> averages = averageCalculator.calculateAverages(points);
        Collection<PointWithValue> uniquePoints = duplicateRemover.removeDuplicates(averages);
    }

}
