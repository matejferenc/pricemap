package com.averagemap.core.generator;

import org.junit.Test;

import com.averagemap.core.average.AverageCalculator;
import com.averagemap.core.average.SimpleSquareAverageCalculator;
import com.averagemap.core.duplicate.AverageResultDuplicateRemover;
import com.averagemap.core.duplicate.DuplicateRemover;

public class SimplePointsPreprocessorTest {

    @Test
    public void test1() {
        AverageCalculator averageCalculator = new SimpleSquareAverageCalculator();
        DuplicateRemover duplicateRemover = new AverageResultDuplicateRemover();
        SimplePointsPreprocessor simplePointsPreprocessor = new SimplePointsPreprocessor(duplicateRemover, averageCalculator);
    }

}
