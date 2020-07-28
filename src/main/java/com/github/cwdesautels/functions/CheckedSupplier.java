package com.github.cwdesautels.functions;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
