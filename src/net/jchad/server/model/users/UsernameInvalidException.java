package net.jchad.server.model.users;

public class UsernameInvalidException extends RuntimeException{
    public UsernameInvalidException() {
        super();
    }

    public UsernameInvalidException(String message) {
        super(message);
    }

    public UsernameInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
