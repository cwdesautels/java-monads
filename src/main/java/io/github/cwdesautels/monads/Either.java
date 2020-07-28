package io.github.cwdesautels.monads;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Right biased either monad.
 */
public interface Either<L, R> {

    // Constructors

    static <L, R> Either<L, R> left(L left) {
        return ImmutableLeft.<L, R>builder()
                .left(left)
                .build();
    }

    static <L, R> Either<L, R> right(R right) {
        return ImmutableRight.<L, R>builder()
                .get(right)
                .build();
    }

    // Behaviour

    L getLeft();

    R get();

    boolean isLeft();

    boolean isRight();

    // Templates

    default R orElse(R other) {
        if (isRight()) {
            return get();
        } else {
            return other;
        }
    }

    default R orElseGet(Supplier<R> other) {
        Objects.requireNonNull(other);

        if (isRight()) {
            return get();
        } else {
            return other.get();
        }
    }

    default R orElseMap(Function<L, R> function) {
        Objects.requireNonNull(function);

        if (isRight()) {
            return get();
        } else {
            return function.apply(getLeft());
        }
    }

    default <X extends Throwable> R orElseThrow(Supplier<X> supplier) throws X {
        Objects.requireNonNull(supplier);

        if (isLeft()) {
            throw supplier.get();
        } else {
            return get();
        }
    }

    default Either<R, L> swap() {
        if (isRight()) {
            return left(get());
        } else {
            return right(getLeft());
        }
    }

    default <T> Either<L, T> map(Function<R, T> function) {
        Objects.requireNonNull(function);

        if (isRight()) {
            return right(function.apply(get()));
        } else {
            return left(getLeft());
        }
    }

    default <T> Either<L, T> flatMap(Function<R, Either<L, T>> function) {
        Objects.requireNonNull(function);

        if (isRight()) {
            return Objects.requireNonNull(function.apply(get()));
        } else {
            return left(getLeft());
        }
    }

    default <T> Either<T, R> mapLeft(Function<L, T> function) {
        Objects.requireNonNull(function);

        if (isLeft()) {
            return left(function.apply(getLeft()));
        } else {
            return right(get());
        }
    }

    default <T> Either<T, R> flatMapLeft(Function<L, Either<T, R>> function) {
        Objects.requireNonNull(function);

        if (isLeft()) {
            return Objects.requireNonNull(function.apply(getLeft()));
        } else {
            return right(get());
        }
    }

    default <T> T fold(Function<L, T> leftMapper, Function<R, T> rightMapper) {
        Objects.requireNonNull(leftMapper);
        Objects.requireNonNull(rightMapper);

        if (isRight()) {
            return rightMapper.apply(get());
        } else {
            return leftMapper.apply(getLeft());
        }
    }

    default Either<L, R> ifRight(Consumer<R> consumer) {
        Objects.requireNonNull(consumer);

        if (isRight()) {
            consumer.accept(get());
        }

        return this;
    }

    default Either<L, R> ifLeft(Consumer<L> consumer) {
        Objects.requireNonNull(consumer);

        if (isLeft()) {
            consumer.accept(getLeft());
        }

        return this;
    }

    default Optional<R> toOptional() {
        if (isRight()) {
            return Optional.ofNullable(get());
        } else {
            return Optional.empty();
        }
    }
}
