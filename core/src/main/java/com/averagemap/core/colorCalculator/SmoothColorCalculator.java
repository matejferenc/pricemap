package com.averagemap.core.colorCalculator;

import javafx.util.Pair;

import java.awt.*;

public class SmoothColorCalculator implements ColorCalculator {

    @Override
    public Color calculate(double value, Pair<Double, Double> minAndMaxValue) {
        double min = minAndMaxValue.getKey();
        double max = minAndMaxValue.getValue();
        double percent = ((value - min) / (max - min));
        percent = Math.sqrt(percent);
        int r = (int) (255 * percent);
        int g = (int) (255 * (1 - percent));
        int b = 0;
        return new Color(r, g, b);
    }
}
