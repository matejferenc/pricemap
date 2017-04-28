package com.averagemap.core.generator.filling;

import com.averagemap.core.coordinates.GoogleMapsPosition;
import com.averagemap.core.coordinates.GoogleMapsTile;
import com.averagemap.core.coordinates.InSquarePosition;

import java.util.function.BiFunction;
import java.util.function.Function;

public class EmptySquareFillingStrategy implements SquareFillingStrategy {

    @Override
    public void fill(GoogleMapsTile tile, BiFunction<InSquarePosition, GoogleMapsPosition, Void> drawPixel, Function<GoogleMapsPosition, Boolean> shouldDraw) {

    }
}
