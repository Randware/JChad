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

        server.startServer();
    }

    @Override
    public void handleFatalError(Exception e) {
        System.out.println("[FATAL ERROR] " + e);
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
