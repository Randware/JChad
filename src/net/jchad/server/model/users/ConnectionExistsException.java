package net.jchad.server.model.users;

public class ConnectionExistsException extends RuntimeException{

    public ConnectionExistsException() {
        super();
    }

    public ConnectionExistsException(String message) {
        super(message);
    }

    public ConnectionExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
