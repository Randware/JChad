package net.jchad.client.view;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.error.MessageHandler;

// Responsible for displaying client output in CLI mode
public class CLI implements MessageHandler {
    private ClientController client;

    public static void main(String[] args) {
        new CLI().runCLI();
    }

    private void runCLI() {
        client = new ClientController(this);

        // Here a new client is created on the backend, that connects to the specified serverAddress and port
        client.startClient(null,0);
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
