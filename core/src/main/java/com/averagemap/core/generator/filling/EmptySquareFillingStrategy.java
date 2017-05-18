package com.averagemap.core.generator.filling;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import com.averagemap.core.valueCalculator.factory.PointValueCalculatorFactory;

import java.util.function.BiFunction;
import java.util.function.Function;

public class EmptySquareFillingStrategy implements SquareFillingStrategy {

    @Override
    public void fill(GoogleMapsTile tile,
                     PointValueCalculatorFactory pointValueCalculatorFactory,
                     BiFunction<PointValueCalculator, GoogleMapsPosition, Void> drawPixel,
                     Function<GoogleMapsPosition, Boolean> shouldDraw) {
    }
}
