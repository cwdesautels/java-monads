package com.github.cwdesautels.monads;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TryTest {
    @Mock
    private Consumer<Object> consumer;

    @Mock
    private Consumer<Throwable> errorConsumer;

    @Mock
    private Function<Throwable, Object> recoveryFunction;

    @Mock
    private Function<Throwable, Try<Object>> exchangeFunction;

    @Test
    public void successDoesNotSupportGetCause() {
        // Then
        assertThrows(UnsupportedOperationException.class, () -> Try.success(new Object()).getCause());
    }

    @Test
    public void failurePropagatesCauseOnGet() {
        // Then
        assertThrows(RuntimeException.class, () -> Try.failure(new IOException("Operation Failed!")).get());
    }

    @Test
    public void successAcceptsNull() {
        // When
        final Try<Void> actual = Try.success(null);
        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertNull(actual.get());
    }

    @Test
    public void failureAcceptsNull() {
        // When
        final Try<Void> actual = Try.failure(null);
        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertNull(actual.getCause());
    }

    @Test
    public void successEqualityBasedOnValue() {
        // When
        final Try<Object> a = Try.success(1);
        final Try<Object> b = Try.success(1);
        final Try<Object> c = Try.success(2);
        // Then
        // Symmetric
        assertEquals(a, b);
        assertEquals(b, a);
        // Reflexive
        assertEquals(a, a);
        // Transitive
        assertNotEquals(a, c);
        assertNotEquals(b, c);
        // Hashcode obligations
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void failuresEqualityBasedOnValue() {
        // When
        final IOException error = new IOException();
        final Try<Object> a = Try.failure(error);
        final Try<Object> b = Try.failure(error);
        final Try<Object> c = Try.failure(new ConcurrentModificationException());
        // Then
        // Symmetric
        assertEquals(a, b);
        assertEquals(b, a);
        // Reflexive
        assertEquals(a, a);
        // Transitive
        assertNotEquals(a, c);
        assertNotEquals(b, c);
        // Hashcode obligations
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void ofHandlesEmptyResult() {
        // Given
        final Try<Object> expected = Try.success(null);
        // When
        final Try<Object> actual = Try.of(() -> null);
        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertNull(actual.get());
        assertEquals(expected, actual);
    }

    @Test
    public void ofHandlesResult() {
        // Given
        final UUID result = UUID.randomUUID();
        final Try<Object> expected = Try.success(result);
        // When
        final Try<Object> actual = Try.of(() -> result);
        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertSame(result, actual.get());
        assertEquals(expected, actual);
    }

    @Test
    public void ofRunnableHandlesResult() {
        // Given
        final Try<Object> expected = Try.success(null);
        // When
        final Try<Void> actual = Try.ofRunnable(() -> {
        });
        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertNull(actual.get());
        assertEquals(expected, actual);
    }

    @Test
    public void ofHandlesError() {
        // Given
        final Exception error = new RuntimeException("Operation Failed!");
        final Try<Object> expected = Try.failure(error);
        // When
        final Try<Object> actual = Try.of(() -> {
            throw error;
        });
        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertSame(error, actual.getCause());
        assertEquals(expected, actual);
    }

    @Test
    public void ofRunnableHandlesError() {
        // Given
        final RuntimeException error = new RuntimeException("Operation Failed!");
        final Try<Object> expected = Try.failure(error);
        // When
        final Try<Void> actual = Try.ofRunnable(() -> {
            throw error;
        });
        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertSame(error, actual.getCause());
        assertEquals(expected, actual);
    }

    @Test
    public void mapOnSuccess() {
        // Given
        final Object result = new Object();
        final Try<List<Object>> expected = Try.success(Collections.singletonList(result));
        // When
        final Try<List<Object>> actual = Try.of(() -> result).map(ImmutableList::of);
        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertEquals(expected, actual);
    }

    @Test
    public void mapOnFailure() {
        // Given
        final Exception result = new IOException();
        final Try<List<Object>> expected = Try.failure(result);
        // When
        final Try<List<Object>> actual = Try.of(() -> {
            throw result;
        }).map(ImmutableList::of);
        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertEquals(expected, actual);
    }

    @Test
    public void flatMapOnSuccess() {
        // Given
        final Object result = new Object();
        final Try<List<Object>> expected = Try.success(Collections.singletonList(result));
        // When
        final Try<List<Object>> actual = Try.of(() -> result).map(ImmutableList::of).flatMap(Try::success);
        // Then
        assertTrue(actual.isSuccess());
        assertFalse(actual.isFailure());
        assertEquals(expected, actual);
    }

    @Test
    public void flatMapOnFailure() {
        // Given
        final Exception result = new IOException();
        final Try<List<Object>> expected = Try.failure(result);
        // When
        final Try<List<Object>> actual = Try.of(() -> {
            throw result;
        }).map(ImmutableList::of).flatMap(Try::success);
        // Then
        assertFalse(actual.isSuccess());
        assertTrue(actual.isFailure());
        assertEquals(expected, actual);
    }

    @Test
    public void orElseOnSuccess() {
        // Given
        final Object expected = 1;
        // When
        final Object actual = Try.of(() -> 1).orElse(2);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void orElseOnFailure() {
        // Given
        final Object expected = 1;
        // When
        final Object actual = Try.of(() -> {
            throw new IOException();
        }).orElse(1);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void orElseGetOnSuccess() {
        // Given
        final Object expected = 1;
        // When
        final Object actual = Try.of(() -> 1).orElseGet(() -> 2);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void orElseGetOnFailure() {
        // Given
        final Object expected = 1;
        // When
        final Object actual = Try.of(() -> {
            throw new IOException();
        }).orElseGet(() -> 1);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void orElseThrowOnSuccess() throws IOException {
        // Given
        final Object expected = 1;
        // When
        final Object actual = Try.of(() -> 1).orElseThrow(IOException::new);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void orElseThrowOnFailure() throws IOException {
        // Then
        assertThrows(IOException.class, () -> Try.of(() -> {
            throw new IOException();
        }).orElseThrow(IOException::new));
    }

    @Test
    public void ifSuccessOnSuccess() {
        // Given
        final Try<Object> monad = Try.of(() -> 1);
        // When
        monad.ifSuccess(consumer);
        // Then
        verify(consumer, times(1)).accept(1);
    }

    @Test
    public void ifSuccessOnFailure() {
        // When
        Try.failure(new IOException()).ifSuccess(consumer);
        // Then
        verify(consumer, never()).accept(any());
    }

    @Test
    public void ifFailureOnSuccess() {
        // When
        Try.of(() -> 1).ifFailure(errorConsumer);
        // Then
        verify(errorConsumer, never()).accept(any());
    }

    @Test
    public void ifFailureOnFailure() {
        // Given
        final Exception error = new IOException();
        // When
        Try.failure(error).ifFailure(errorConsumer);
        // Then
        verify(errorConsumer, times(1)).accept(error);
    }

    @Test
    public void recoverOnSuccess() {
        // When
        Try.of(Object::new).recover(recoveryFunction);
        // Then
        verify(recoveryFunction, never()).apply(any());
    }

    @Test
    public void recoverOnFailure() {
        // Given
        final Exception error = new IOException();
        // When
        Try.failure(error).recover(recoveryFunction);
        // Then
        verify(recoveryFunction, times(1)).apply(error);
    }

    @Test
    public void exchangeOnSuccess() {
        // When
        Try.of(Object::new).exchange(exchangeFunction);
        // Then
        verify(exchangeFunction, never()).apply(any());
    }

    @Test
    public void exchangeOnFailure() {
        // Given
        final Exception error = new IOException();
        // When
        when(exchangeFunction.apply(error)).thenReturn(Try.success(true));
        Try.failure(error).exchange(exchangeFunction);
        // Then
        verify(exchangeFunction, times(1)).apply(error);
    }

    @Test
    public void toOptionalOnSuccess() {
        // Given
        final Optional<Boolean> expected = Optional.of(true);
        // When
        final Optional<Boolean> actual = Try.of(() -> true).toOptional();
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void toOptionalOnFailure() {
        // Given
        final Optional<Boolean> expected = Optional.empty();
        // When
        final Try<Boolean> actual = Try.failure(new IOException());
        // Then
        assertEquals(expected, actual.toOptional());
    }

    @Test
    public void toEitherOnSuccess() {
        // Given
        final Either<Throwable, Boolean> expected = Either.right(true);
        // When
        final Either<Throwable, Boolean> actual = Try.of(() -> true).toEither();
        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void toEitherOnFailure() {
        // Given
        final Exception error = new IOException();
        final Either<Throwable, Boolean> expected = Either.left(error);
        // When
        final Try<Boolean> actual = Try.failure(error);
        // Then
        assertEquals(expected, actual.toEither());
    }
}
