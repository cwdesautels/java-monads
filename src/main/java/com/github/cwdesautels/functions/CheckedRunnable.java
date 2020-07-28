package com.github.cwdesautels.functions;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
