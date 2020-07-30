/*
 * MIT License
 *
 * Copyright (c) 2020 Carl Desautels
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.cwdesautels.monad;

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
                .right(right)
                .build();
    }

    // Behaviour

    L left();

    R right();

    boolean isLeft();

    boolean isRight();

    // Templates

    default L getLeft() {
        return left();
    }

    default R get() {
        return right();
    }

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

    default <T> T fold(Function<L, T> leftMapper, Function<R, T> rightMapper) {
        Objects.requireNonNull(leftMapper);
        Objects.requireNonNull(rightMapper);

        if (isRight()) {
            return rightMapper.apply(get());
        } else {
            return leftMapper.apply(getLeft());
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
