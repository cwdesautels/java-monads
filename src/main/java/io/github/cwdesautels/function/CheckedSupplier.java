package io.github.cwdesautels.function;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
