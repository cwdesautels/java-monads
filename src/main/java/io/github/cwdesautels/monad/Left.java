package io.github.cwdesautels.monad;

import io.github.cwdesautels.annotation.Nullable;
import org.immutables.value.Value;

import java.util.NoSuchElementException;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface Left<L, R> extends Either<L, R> {
    @Override
    @Value.Auxiliary
    default R get() {
        throw new NoSuchElementException();
    }

    @Override
    @Nullable
    L getLeft();

    @Override
    default boolean isLeft() {
        return true;
    }

    @Override
    default boolean isRight() {
        return false;
    }
}
