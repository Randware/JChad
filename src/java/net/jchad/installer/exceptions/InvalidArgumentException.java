package net.jchad.installer.exceptions;



import java.util.function.Predicate;

public class InvalidArgumentException extends Exception{
    //TODO test constructor

    /**
     * This is a custom exception in order to handle wrong input.
     * I decided to create this Exception instead of using IllegalArgumentException,
     * because the IllegalArgumentException is a RuntimeException.
     * And I want that this Exception get 100% handled
     */
    public InvalidArgumentException (String message){
        super(message);
    }

    /**
     * unsure if this Constructor work the intended way
     * @param reason
     * @param message
     */
    public InvalidArgumentException (String reason, String message) {
        super(message, new Throwable(reason));
    }

    /**
     *  This methode replaces the following statement:
     *  if (condition) throw new InvalidArgumentException
     *  by just providing this method
     *
     * @param value The item that gets "tested" with the given predicate
     * @param predicate The predicate that tests the "item"
     * @param message The message that the Exception has IF the Predicate is tre
     * @param <T>
     * @throws InvalidArgumentException
     */
    public static <T> void throwIfTrue(T value, Predicate<T> predicate, String message) throws InvalidArgumentException{
        if (predicate.test(value)) throw new InvalidArgumentException(message);

    }

    public static <T> void throwIfFalse(T value,Predicate<T> predicate, String message) throws InvalidArgumentException{
        throwIfTrue(value, predicate.negate(), message);
    }
}
