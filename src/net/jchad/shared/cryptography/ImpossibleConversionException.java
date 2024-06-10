package net.jchad.shared.cryptography;

public class ImpossibleConversionException extends Exception{
    public ImpossibleConversionException() {
        super();
    }

    /**
     * Constructs an {@code ImpossibleConversionException} with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */

    public ImpossibleConversionException(String s) {
        super(s);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A {@code null} value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since 1.5
     */
    public ImpossibleConversionException(String message, Throwable cause) {
        super(message, cause);
    }
    public ImpossibleConversionException(Throwable e) {super(e);}
}
