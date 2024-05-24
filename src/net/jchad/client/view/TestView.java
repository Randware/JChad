package net.jchad.client.view;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;

public class TestView implements ViewCallback {
    private ClientController controller;

    public static void main(String[] args) {
        new TestView().run();
    }

    private void run() {
        this.controller = new ClientController(this);
        controller.connect(new ConnectionDetails("test", "127.0.0.1", 13814, "user", "test"));
    }

    @Override
    public void handleFatalError(Exception e) {
        System.out.println("[Fatal error] " + e.getMessage());

        e.printStackTrace();
    }

    @Override
    public void handleError(Exception e) {
        System.out.println("[Error] " + e.getMessage());

        e.printStackTrace();
    }

    @Override
    public void handleWarning(String warning) {
        System.out.println("[Warning] " + warning);
    }

    @Override
    public void handleInfo(String info) {
        System.out.println("[Info] " + info);
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
