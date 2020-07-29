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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.UUID;

import static io.github.cwdesautels.monad.Either.left;
import static io.github.cwdesautels.monad.Either.right;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EitherTest {
    @Test
    void shallSupportEqualityAmongstLeft() {
        // Given
        final Either<UUID, UUID> a = ImmutableLeft.<UUID, UUID>builder()
                .left(randomUUID())
                .build();
        final Either<UUID, UUID> b = ImmutableLeft.<UUID, UUID>builder()
                .left(a.getLeft())
                .build();
        final Either<UUID, UUID> c = ImmutableLeft.<UUID, UUID>builder()
                .left(randomUUID())
                .build();

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
        final Either<UUID, UUID> a = ImmutableRight.<UUID, UUID>builder()
                .right(randomUUID())
                .build();
        final Either<UUID, UUID> b = ImmutableRight.<UUID, UUID>builder()
                .right(a.get())
                .build();
        final Either<UUID, UUID> c = ImmutableRight.<UUID, UUID>builder()
                .right(randomUUID())
                .build();

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
        final Either<UUID, UUID> expected = ImmutableLeft.<UUID, UUID>builder()
                .left(value)
                .build();

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
        final Either<UUID, UUID> expected = ImmutableRight.<UUID, UUID>builder()
                .right(value)
                .build();

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
    void shallSwapValues() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = ImmutableLeft.<UUID, UUID>builder()
                .left(value)
                .build();

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
    void shallMapWhenRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = ImmutableRight.<UUID, UUID>builder()
                .right(value)
                .build();

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(randomUUID()).map(random -> value);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotMapWhenLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = ImmutableLeft.<UUID, UUID>builder()
                .left(value)
                .build();

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(value).map(uuid -> randomUUID());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallFlatMapWhenRight() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = ImmutableLeft.<UUID, UUID>builder()
                .left(value)
                .build();

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>right(randomUUID()).flatMap(random -> left(value));

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void shallNotFlatMapWhenLeft() {
        // Given
        final UUID value = randomUUID();
        final Either<UUID, UUID> expected = ImmutableLeft.<UUID, UUID>builder()
                .left(value)
                .build();

        // When
        final Either<UUID, UUID> actual = Either.<UUID, UUID>left(value).flatMap(uuid -> right(randomUUID()));

        // Then
        assertEquals(expected, actual);
    }
}
