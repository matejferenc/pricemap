package com.averagemap.core.generator.filling;

import java.util.function.Function;

@FunctionalInterface
public interface SquareFillingStrategy {

    void fill(Function<Void, Void> fillingCode);

}
