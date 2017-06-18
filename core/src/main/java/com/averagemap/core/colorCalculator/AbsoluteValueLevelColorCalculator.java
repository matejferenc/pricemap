package com.averagemap.core.colorCalculator;

import javafx.util.Pair;

import java.awt.*;

public class AbsoluteValueLevelColorCalculator implements ColorCalculator {

    private Color c1 = new Color(196, 35, 40);
    private Color c2 = new Color(243, 89, 37);
    private Color c3 = new Color(251, 146, 31);
    private Color c4 = new Color(131, 199, 80);
    private Color c5 = new Color(56, 185, 69);
    private Color c6 = new Color(1, 103, 55);

    private double l1 = 4000;
    private double l2 = 3000;
    private double l3 = 2000;
    private double l4 = 1000;
    private double l5 = 500;
    private double l6 = 100;

    @Override
    public Color calculate(double value, Pair<Double, Double> minAndMaxValue) {
        if (value < l6) return c6;
        else if (value < l5) return c5;
        else if (value < l4) return c4;
        else if (value < l3) return c3;
        else if (value < l2) return c2;
        else return c1;
    }
}
