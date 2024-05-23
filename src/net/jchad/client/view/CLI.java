package net.jchad.client.view;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChatMessage;

/**
 * Responsible for displaying the client output in CLI mode.
 * This class is only really used in development process,
 * while the GUI is still under construction.
 */
public class CLI implements ViewCallback {
    private ClientController client;

    public static void main(String[] args) {
        new CLI().runCLI();
    }

    private void runCLI() {
        client = new ClientController(this);
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

    @Override
    public String displayPrompt(String promptTitle, String promptMessage) {
        return "";
    }

    @Override
    public void displayOwnMessage(ClientChatMessage message) {

    }

    @Override
    public void displayOtherMessage(ClientChatMessage message) {

    }

    @Override
    public void updateDisplay() {

    }
}
