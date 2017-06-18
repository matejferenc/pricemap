package com.averagemap.core.generator.filling;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import com.averagemap.core.valueCalculator.PointValueCalculatorFactory;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface SquareFillingStrategy {

    void fill(GoogleMapsTile tile,
              PointValueCalculatorFactory pointValueCalculatorFactory,
              BiFunction<PointValueCalculator, GoogleMapsPosition, Void> drawPixel,
              Function<GoogleMapsPosition, Boolean> shouldDraw);

}
