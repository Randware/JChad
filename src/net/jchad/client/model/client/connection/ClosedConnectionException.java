package net.jchad.client.model.client.connection;

public class ClosedConnectionException extends RuntimeException {
    public ClosedConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClosedConnectionException(String message) {
        super(message);
    }
}
