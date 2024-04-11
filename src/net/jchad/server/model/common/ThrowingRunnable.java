package net.jchad.server.model.common;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
