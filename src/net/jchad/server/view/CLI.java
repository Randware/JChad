package net.jchad.server.view;

import net.jchad.server.controller.ServerController;
import net.jchad.server.model.error.MessageHandler;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Arrays;

// Responsible for displaying server output in CLI mode
public class CLI implements MessageHandler {
    private ServerController server;

    public static void main(String[] args) {
        new CLI().runCLI();
    }

    private void runCLI() {
        server = new ServerController(this);

        server.startServer();
    }

    @Override
    public void handleFatalError(Exception e) {
        System.out.println("[FATAL ERROR] " + Arrays.toString(e.getStackTrace()));
        System.out.println("STOPPING EXECUTION");
        System.exit(1);
    }

    @Override
    public void handleError(Exception e) {
        System.out.println("[ERROR] " + e);
    }

    @Override
    public void handleWarning(String warning) {
        System.out.println("[WARNING] " + warning);
    }

    @Override
    public void handleInfo(String info) {
        System.out.println("[INFO] " + info);
    }

}
