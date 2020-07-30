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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.UUID;

import static io.github.cwdesautels.monad.Either.left;
import static io.github.cwdesautels.monad.Either.right;
import static io.github.cwdesautels.monad.Try.failure;
import static io.github.cwdesautels.monad.Try.of;
import static io.github.cwdesautels.monad.Try.ofRunnable;
import static io.github.cwdesautels.monad.Try.success;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class TryTest {
    @Test
    void shallSupportEqualityAmongstSuccess() {
        // Given
        final Try<Object> a = success(1);
        final Try<Object> b = success(1);
        final Try<Object> c = success(2);

        // Then assert symmetric equality
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());

        // Then assert reflexive equality
        assertEquals(a, a);

        // Then assert transitive equality
        assertNotEquals(a, c);
        assertNotEquals(b, c);
    }

    @Test
    void shallSupportEqualityAmongstFailure() {
        // When
        final IOException error = new IOException();
        final Try<Object> a = failure(error);
        final Try<Object> b = failure(error);
        final Try<Object> c = failure(new ConcurrentModificationException());

        // Then assert symmetric equality
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());

        // Then assert reflexive equality
        assertEquals(a, a);

        // Then assert transitive equality
        assertNotEquals(a, c);
        assertNotEquals(b, c);
    }

    @Test
    void shallReturnSuccess() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);

        // When
        final Try<UUID> actual = success(value);

        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertEquals(expected, actual);
        assertSame(expected.get(), actual.get());
        assertThrows(UnsupportedOperationException.class, actual::getCause);
    }

    @Test
    void shallReturnFailure() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<UUID> expected = failure(value);

        // When
        final Try<UUID> actual = failure(value);

        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertEquals(expected, actual);
        assertSame(expected.getCause(), actual.getCause());
        assertThrows(RuntimeException.class, actual::get);
    }

    @Test
    void shallReturnSuccessFromSupplier() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);

        // When
        final Try<UUID> actual = of(() -> value);

        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertEquals(expected, actual);
        assertSame(expected.get(), actual.get());
        assertThrows(UnsupportedOperationException.class, actual::getCause);
    }

    @Test
    void shallReturnErrorFromSupplier() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<UUID> expected = failure(value);

        // When
        final Try<UUID> actual = of(() -> {
            throw value;
        });

        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertEquals(expected, actual);
        assertSame(expected.getCause(), actual.getCause());
        assertThrows(RuntimeException.class, actual::get);
    }

    @Test
    void shallReturnSuccessFromRunnable() {
        // Given
        final Try<Void> expected = success(null);

        // When
        final Try<Void> actual = ofRunnable(() -> {
        });

        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertEquals(expected, actual);
        assertSame(expected.get(), actual.get());
        assertThrows(UnsupportedOperationException.class, actual::getCause);
    }

    @Test
    void shallReturnErrorFromRunnable() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<Void> expected = failure(value);

        // When
        final Try<Void> actual = ofRunnable(() -> {
            throw value;
        });

        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertEquals(expected, actual);
        assertSame(expected.getCause(), actual.getCause());
        assertThrows(RuntimeException.class, actual::get);
    }


    @Test
    void shallMapWhenSuccess() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);
        // When
        final Try<UUID> actual = success(randomUUID()).map(random -> value);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotMapWhenFailure() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<Object> expected = failure(value);

        // When
        final Try<Object> actual = failure(value).map(uuid -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallFlatMapWhenSuccess() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);

        // When
        final Try<UUID> actual = success(randomUUID()).flatMap(random -> success(value));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotFlatMapWhenFailure() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<UUID> expected = failure(value);

        // When
        final Try<UUID> actual = failure(value).flatMap(uuid -> success(randomUUID()));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnSuccessWhenSuccessOrElse() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = success(expected).orElse(randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnValueWhenFailureOrElse() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Try.<UUID>failure(new IOException("I broke :(")).orElse(expected);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnSuccessWhenSuccessOrElseGet() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = success(expected).orElseGet(UUID::randomUUID);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnSuccessWhenFailureOrElseGet() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Try.<UUID>failure(new IOException("I broke :(")).orElseGet(() -> expected);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnSuccessWhenRightOrElseThrow() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = success(expected).orElseThrow(IllegalStateException::new);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnValueWhenFailureOrElseThrow() {
        // Given
        final IllegalStateException expected = new IllegalStateException();

        // When
        final Try<UUID> actual = failure(new IOException("I broke :("));

        // Then
        assertThrows(expected.getClass(), () -> actual.orElseThrow(uuid -> expected));
    }


    @Test
    void shallConsumerSuccess() {
        // Given
        final UUID expected = randomUUID();

        // Then
        success(expected)
                .ifFailure(actual -> fail())
                .ifSuccess(actual -> assertEquals(expected, actual));
    }

    @Test
    void shallConsumerFailure() {
        // Given
        final Exception expected = new IOException("I broke :(");

        // Then
        Try.<UUID>failure(expected)
                .ifSuccess(actual -> fail())
                .ifFailure(actual -> assertEquals(expected, actual));
    }

    @Test
    void shallIgnoreRecoverWhenSuccess() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);
        // When
        final Try<UUID> actual = success(value).recover(error -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallRecoverWhenFailure() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);

        // When
        final Try<UUID> actual = Try.<UUID>failure(new IOException("I broke :(")).recover(error -> value);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallIgnoreRecoverWhenFailureMismatch() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<UUID> expected = failure(value);

        // When
        final Try<UUID> actual = Try.<UUID>failure(value)
                .recoverWhen(error -> false, error -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallIgnoreExchangeWhenSuccess() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);
        // When
        final Try<UUID> actual = success(value).exchange(error -> success(randomUUID()));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallExchangeWhenFailure() {
        // Given
        final UUID value = randomUUID();
        final Try<UUID> expected = success(value);

        // When
        final Try<UUID> actual = Try.<UUID>failure(new IOException("I broke :(")).exchange(error -> success(value));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallIgnoreExchangeWhenFailureMismatch() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Try<UUID> expected = failure(value);

        // When
        final Try<UUID> actual = Try.<UUID>failure(value)
                .exchangeWhen(error -> false, error -> success(randomUUID()));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallCollapseSuccessToEither() {
        // Given
        final UUID value = randomUUID();
        final Either<Throwable, UUID> expected = right(value);

        // When
        final Either<Throwable, UUID> actual = success(value).toEither();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallCollapseFailureToEither() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Either<Throwable, UUID> expected = left(value);

        // When
        final Either<Throwable, UUID> actual = Try.<UUID>failure(value).toEither();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallCollapseSuccessToOptional() {
        // Given
        final UUID value = randomUUID();
        final Optional<UUID> expected = of(value);

        // When
        final Optional<UUID> actual = success(value).toOptional();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallCollapseFailureToEmptyOptional() {
        // Given
        final Exception value = new IOException("I broke :(");
        final Optional<UUID> expected = empty();

        // When
        final Optional<UUID> actual = Try.<UUID>failure(value).toOptional();

        // Then
        assertEquals(expected, actual);
    }
}
