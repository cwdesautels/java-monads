package io.github.cwdesautels.function;

@FunctionalInterface
public interface CheckedFunction<I, O> {
    O apply(I input) throws Exception;
}
