package net.jchad.server.model.error;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

/* Is used by the server to send messages to the view,
which then handles messages based on their types */
public interface MessageHandler {
    // When a fatal error occurs, the program execution can't continue
    void handleFatalError(Exception e);

    // This error is non-fatal, but should be acknowledged
    void handleError(Exception e);

    // A warning should be displayed, but can be ignored
    void handleWarning(String warning);

    // An info used for displaying status, info, etc.
    void handleInfo(String info);

    void handleDebug(String debug);

}
