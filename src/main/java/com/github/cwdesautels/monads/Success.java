package com.github.cwdesautels.monads;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface Success<T> extends Try<T> {
    @Override
    @Nullable
    T get();

    @Override
    @Value.Auxiliary
    default Exception getCause() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isSuccess() {
        return true;
    }

    @Override
    default boolean isFailure() {
        return false;
    }
}
