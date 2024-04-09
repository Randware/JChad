package net.jchad.server.view;

import net.jchad.server.controller.ServerController;
import net.jchad.server.model.error.MessageHandler;

// Responsible for displaying output in GUI mode
public class GUI implements MessageHandler {
    private ServerController server;

    public static void main(String[] args) {
        new GUI().runGUI();
    }

    private void runGUI() {
        server = new ServerController(this);

        // Display GUI
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
