package com.github.cwdesautels.monads;

import org.immutables.value.Value;

import java.util.NoSuchElementException;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface Right<L, R> extends Either<L, R> {
    @Override
    @Nullable
    R get();

    @Override
    @Value.Auxiliary
    default L getLeft() {
        throw new NoSuchElementException();
    }

    @Override
    default boolean isLeft() {
        return false;
    }

    @Override
    default boolean isRight() {
        return true;
    }
}
