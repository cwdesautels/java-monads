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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static io.github.cwdesautels.monad.Either.left;
import static io.github.cwdesautels.monad.Either.right;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EitherTest {
    @Test
    void shallSupportEqualityAmongstLeft() {
        // Given
        final Either<UUID, UUID> a = left(randomUUID());
        final Either<UUID, UUID> b = left(a.left());
        final Either<UUID, UUID> c = left(randomUUID());

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
    void shallSupportEqualityAmongstRight() {
        // Given
        final Either<UUID, UUID> a = right(randomUUID());
        final Either<UUID, UUID> b = right(a.right());
        final Either<UUID, UUID> c = right(randomUUID());

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
    void shallReturnLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = left(value);

        // When
        final Either<UUID, UUID> actual = left(value);

        // Then
        assertTrue(actual.isLeft());
        assertFalse(actual.isRight());
        assertEquals(expected, actual);
        assertSame(expected.getLeft(), actual.getLeft());
        assertThrows(NoSuchElementException.class, actual::get);
    }

    @Test
    void shallReturnRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = right(value);

        // When
        final Either<UUID, UUID> actual = right(value);

        // Then
        assertTrue(actual.isRight());
        assertFalse(actual.isLeft());
        assertEquals(expected, actual);
        assertSame(expected.get(), actual.get());
        assertThrows(NoSuchElementException.class, actual::getLeft);
    }

    @Test
    void shallReturnRightWhenRightOrElse() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = right(expected).orElse(randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnValueWhenLeftOrElse() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Either.<UUID, UUID>left(randomUUID()).orElse(expected);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnRightWhenRightOrElseGet() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = right(expected).orElseGet(UUID::randomUUID);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnValueWhenLeftOrElseGet() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Either.<UUID, UUID>left(randomUUID()).orElseGet(() -> expected);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnRightWhenRightOrElseMap() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = right(expected).orElseMap(left -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnValueWhenLeftOrElseMap() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Either.<UUID, UUID>left(randomUUID()).orElseMap(left -> expected);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnRightWhenRightOrElseThrow() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = right(expected).orElseThrow(IllegalStateException::new);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallReturnValueWhenLeftOrElseThrow() {
        // Given
        final IllegalStateException expected = new IllegalStateException();

        // When
        final Either<UUID, UUID> actual = Either.left(randomUUID());

        // Then
        assertThrows(expected.getClass(), () -> actual.orElseThrow(() -> expected));
    }

    @Test
    void shallSwapLeftToRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = left(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(value).swap();

        // Then
        assertTrue(actual.isLeft());
        assertFalse(actual.isRight());
        assertEquals(expected, actual);
        assertSame(expected.getLeft(), actual.getLeft());
        assertThrows(NoSuchElementException.class, actual::get);
    }

    @Test
    void shallSwapRightToLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = right(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(value).swap();

        // Then
        assertTrue(actual.isRight());
        assertFalse(actual.isLeft());
        assertEquals(expected, actual);
        assertSame(expected.get(), actual.get());
        assertThrows(NoSuchElementException.class, actual::getLeft);
    }

    @Test
    void shallMapWhenRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = right(value);
        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(randomUUID()).map(random -> value);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotMapWhenLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = left(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(value).map(uuid -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallFlatMapWhenRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = left(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(randomUUID()).flatMap(random -> left(value));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotFlatMapWhenLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = left(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(value).flatMap(uuid -> right(randomUUID()));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallMapLeftWhenLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = left(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(randomUUID()).mapLeft(random -> value);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotMapLeftWhenRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = right(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(value).mapLeft(uuid -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallFlatMapLeftWhenLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = right(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(randomUUID()).flatMapLeft(random -> right(value));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotFlatMapLeftWhenRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = right(value);

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(value).flatMapLeft(uuid -> left(randomUUID()));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallFoldRightWhenRight() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Either.<UUID, UUID>right(randomUUID()).fold(Function.identity(), random -> expected);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallFoldLeftWhenLeft() {
        // Given
        final UUID expected = randomUUID();

        // When
        final UUID actual = Either.<UUID, UUID>left(randomUUID()).fold(random -> expected, Function.identity());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallConsumerRightWhenRight() {
        // Given
        final UUID expected = randomUUID();

        // Then
        Either.<UUID, UUID>right(expected)
                .ifLeft(actual -> fail())
                .ifRight(actual -> assertEquals(expected, actual));
    }

    @Test
    void shallConsumerLeftWhenLeft() {
        // Given
        final UUID expected = randomUUID();

        // Then
        Either.<UUID, UUID>left(expected)
                .ifRight(actual -> fail())
                .ifLeft(actual -> assertEquals(expected, actual));
    }

    @Test
    void shallCollapseRightToOptional() {
        // Given
        final UUID value = randomUUID();
        final Optional<UUID> expected = Optional.of(value);

        // When
        final Optional<UUID> actual = right(value).toOptional();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallCollapseLeftToEmptyOptional() {
        // Given
        final UUID value = randomUUID();
        final Optional<UUID> expected = Optional.empty();

        // When
        final Optional<UUID> actual = Either.<UUID, UUID>left(value).toOptional();

        // Then
        assertEquals(expected, actual);
    }
}
