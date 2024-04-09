package net.jchad.server.view;

import net.jchad.server.controller.ServerController;
import net.jchad.server.model.error.MessageHandler;

// Responsible for displaying output in CLI mode
public class CLI implements MessageHandler {
    private ServerController server;

    public static void main(String[] args) {
        new CLI().runCLI();
    }

    private void runCLI() {
        server = new ServerController(this);

        // Display CLI
    }

    @Override
    public void handleFatalError(Exception e) {

    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void handleWarning(String warning) {

    }

    @Override
    public void handleInfo(String info) {

    }
}
