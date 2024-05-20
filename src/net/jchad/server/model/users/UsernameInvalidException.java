package net.jchad.server.model.users;

public class UsernameInvalidException extends RuntimeException{
    private final String invalidUsername;

    public UsernameInvalidException(String invalidUsername) {
        super();
        this.invalidUsername = invalidUsername;
    }



    public UsernameInvalidException(String message, String invalidUsername) {
        super(message);
        this.invalidUsername = invalidUsername;
    }

    public UsernameInvalidException(String message, Throwable cause, String invalidUsername) {
        super(message, cause);
        this.invalidUsername = invalidUsername;
    }

    public String getInvalidUsername() {
        return invalidUsername;
    }
}
