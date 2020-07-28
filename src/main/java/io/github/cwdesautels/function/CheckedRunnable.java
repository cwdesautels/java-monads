package io.github.cwdesautels.function;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
