package net.jchad.server.model.common;

/**
 * Similar to {@link java.lang.Runnable} interface, but throws an {@link java.lang.Exception}
 */

@FunctionalInterface
public interface ThrowingRunnable {

    /**
     * Runnable code
     *
     * @throws Exception
     */
    void run() throws Exception;
}
