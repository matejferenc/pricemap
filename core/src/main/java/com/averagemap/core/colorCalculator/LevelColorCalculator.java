package com.averagemap.core.colorCalculator;

import javafx.util.Pair;

import java.awt.*;

public class LevelColorCalculator implements ColorCalculator {

    private Color c1 = new Color(196, 35, 40);
    private Color c2 = new Color(243, 89, 37);
    private Color c3 = new Color(251, 146, 31);
    private Color c4 = new Color(131, 199, 80);
    private Color c5 = new Color(56, 185, 69);
    private Color c6 = new Color(1, 103, 55);

    @Override
    public Color calculate(double value, Pair<Double, Double> minAndMaxValue) {
        double min = minAndMaxValue.getKey();
        double max = minAndMaxValue.getValue();
        float percent = (float) ((value - min) / (max - min));
        if (percent < 0.10) return c6;
        else if (percent < 0.20) return c5;
        else if (percent < 0.35) return c4;
        else if (percent < 0.50) return c3;
        else if (percent < 0.65) return c2;
        else return c1;
    }
}
