package io.github.cwdesautels.monad;

import io.github.cwdesautels.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface Failure<T> extends Try<T> {
    @Override
    @Value.Auxiliary
    default T get() {
        throw new RuntimeException(this.getCause());
    }

    @Override
    @Nullable
    Throwable getCause();

    @Override
    default boolean isSuccess() {
        return false;
    }

    @Override
    default boolean isFailure() {
        return true;
    }
}