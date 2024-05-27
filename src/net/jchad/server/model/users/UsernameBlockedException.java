package net.jchad.server.model.users;

public class UsernameBlockedException extends RuntimeException{
    public UsernameBlockedException() {
        super();
    }

    public UsernameBlockedException(String message) {
        super(message);
    }

    public UsernameBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
