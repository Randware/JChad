package net.jchad.shared.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class provides commonly used utility code.
 */
public class Util {
    /**
     * This method converts an exceptions StackTrace into a string.
     *
     * @param e the exception that should be converted
     * @return the converted string
     */
    public static String stackTraceToString(Exception e) {
        StringWriter stringWriter = new StringWriter(); // These 3 lines convert the stacktrace of the exception into a string
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
