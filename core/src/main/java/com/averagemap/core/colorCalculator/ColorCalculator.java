package com.averagemap.core.colorCalculator;

import javafx.util.Pair;

import java.awt.*;

public interface ColorCalculator {

    Color calculate(double value, Pair<Double, Double> minAndMaxValue);
}
