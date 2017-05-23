package com.averagemap.core.generator.filling;

import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.valueCalculator.PointValueCalculator;
import com.averagemap.core.valueCalculator.factory.PointValueCalculatorFactory;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;

public class FullSquareFillingStrategy implements SquareFillingStrategy {

    @Override
    public void fill(GoogleMapsTile tile,
                     PointValueCalculatorFactory pointValueCalculatorFactory,
                     BiFunction<PointValueCalculator, GoogleMapsPosition, Void> drawPixel,
                     Function<GoogleMapsPosition, Boolean> shouldDraw) {
        PointValueCalculator pointValueCalculator = pointValueCalculatorFactory.create(tile);
        IntStream.range(0, TILE_SIZE)
                .parallel()
                .forEach(i -> {
                    IntStream.range(0, TILE_SIZE)
                            .parallel()
                            .forEach(j -> {
                                GoogleMapsPosition pixelPosition = new GoogleMapsPosition(tile.getX() * TILE_SIZE + i, tile.getY() * TILE_SIZE + j, tile.getZoom());
                                drawPixel.apply(pointValueCalculator, pixelPosition);
                            });
                });
    }
}
