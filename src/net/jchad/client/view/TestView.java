package net.jchad.client.view;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;

public class TestView implements ViewCallback {
    private ClientController controller;

    public static void main(String[] args) {
        new TestView().run();
    }

    private void run() {
        this.controller = new ClientController(this);
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
}
