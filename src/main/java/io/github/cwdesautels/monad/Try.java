package io.github.cwdesautels.monad;

import io.github.cwdesautels.function.CheckedFunction;
import io.github.cwdesautels.function.CheckedRunnable;
import io.github.cwdesautels.function.CheckedSupplier;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Eager opinionated try monad.
 */
public interface Try<T> {

    // Constructors

    static <T> Try<T> of(CheckedSupplier<T> supplier) {
        Objects.requireNonNull(supplier);

        try {
            return success(supplier.get());
        } catch (Exception e) {
            return failure(e);
        }
    }

    static Try<Void> ofRunnable(CheckedRunnable runnable) {
        Objects.requireNonNull(runnable);

        return of(() -> {
            runnable.run();

            return null;
        });
    }

    static <T> Try<T> success(T value) {
        return ImmutableSuccess.<T>builder()
                .get(value)
                .build();
    }

    static <T> Try<T> failure(Throwable error) {
        return ImmutableFailure.<T>builder()
                .cause(error)
                .build();
    }

    // Behaviour

    T get();

    Throwable getCause();

    boolean isSuccess();

    boolean isFailure();

    // Templates

    default <R> Try<R> map(CheckedFunction<T, R> function) {
        Objects.requireNonNull(function);

        if (isSuccess()) {
            return of(() -> function.apply(get()));
        } else {
            return failure(getCause());
        }
    }

    default <R> Try<R> flatMap(CheckedFunction<T, Try<R>> function) {
        Objects.requireNonNull(function);

        if (isSuccess()) {
            return of(() -> function.apply(get()))
                    .map(Objects::requireNonNull)
                    .map(Try::get);
        } else {
            return failure(getCause());
        }
    }

    default T orElse(T other) {
        if (isFailure()) {
            return other;
        } else {
            return get();
        }
    }

    default T orElseGet(Supplier<T> other) {
        Objects.requireNonNull(other);

        if (isFailure()) {
            return other.get();
        } else {
            return get();
        }
    }

    default <X extends Throwable> T orElseThrow(Function<Throwable, X> mapper) throws X {
        Objects.requireNonNull(mapper);

        if (isFailure()) {
            throw mapper.apply(getCause());
        } else {
            return get();
        }
    }

    default Try<T> ifSuccess(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);

        if (isSuccess()) {
            consumer.accept(get());
        }

        return this;
    }

    default Try<T> ifFailure(Consumer<Throwable> consumer) {
        Objects.requireNonNull(consumer);

        if (isFailure()) {
            consumer.accept(getCause());
        }

        return this;
    }

    default Try<T> recover(CheckedFunction<Throwable, T> function) {
        return recoverWhen(t -> true, function);
    }

    default Try<T> recoverWhen(Predicate<Throwable> predicate, CheckedFunction<Throwable, T> function) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(function);

        if (isSuccess() || !predicate.test(getCause())) {
            return this;
        } else {
            return of(() -> function.apply(getCause()));
        }
    }

    default Try<T> exchange(CheckedFunction<Throwable, Try<T>> function) {
        return exchangeWhen(t -> true, function);
    }

    default Try<T> exchangeWhen(Predicate<Throwable> predicate, CheckedFunction<Throwable, Try<T>> function) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(function);

        if (isSuccess() || !predicate.test(getCause())) {
            return this;
        } else {
            return of(() -> function.apply(getCause()))
                    .map(Objects::requireNonNull)
                    .map(Try::get);
        }
    }

    default Either<Throwable, T> toEither() {
        if (isSuccess()) {
            return Either.right(get());
        } else {
            return Either.left(getCause());
        }
    }

    default Optional<T> toOptional() {
        if (isSuccess()) {
            return Optional.ofNullable(get());
        } else {
            return Optional.empty();
        }
    }
}