package io.github.cwdesautels.functions;

@FunctionalInterface
public interface CheckedFunction<I, O> {
    O apply(I input) throws Exception;
}
