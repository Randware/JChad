package net.jchad.server.model.server;

/**
 * This exception gets thrown when the connection gets closed.
 */
public class ConnectionClosedException extends RuntimeException{
    public ConnectionClosedException() {
        super();
    }

    public ConnectionClosedException(String message) {
        super(message);
    }

    public ConnectionClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
