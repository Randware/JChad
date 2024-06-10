package net.jchad.tests.client;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class TestConnection implements ViewCallback, Runnable {

    public static Integer username = 0;
    private SecureRandom random = new SecureRandom();

    @Override
    public void run() {
        String username = "";
        synchronized (TestConnection.username) {
            username = TestConnection.username++ + "";
        }
        ClientController client = new ClientController(this);
        client.connect(new ConnectionDetails(
                "test",
                    "localhost",
                        13814,
                username,
                            ""
        ));
        ClientChat chat = client.getChat("general");
        client.setCurrentChat(chat);
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(1,50));
                client.sendMessage("Hi I am " + username + ". I love JChad");
                System.out.println(username + " sent a message!");
            } catch (InterruptedException e) {

            }

        }

    }

    @Override
    public void handleFatalError(Exception e) {
        System.out.println(Thread.currentThread().getName() + " fatal error" + ": " + e.getMessage());
    }

    @Override
    public void handleError(Exception e) {
        System.out.println(Thread.currentThread().getName() + " error" + ": " + e.getMessage());
    }

    @Override
    public void handleWarning(String warning) {
        System.out.println(Thread.currentThread().getName() + " warning" + ": " + warning);
    }

    @Override
    public void handleInfo(String info) {
        System.out.println(Thread.currentThread().getName() + " info" + ": " + info);
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
        System.out.println(Thread.currentThread().getName() + " received a message: " + message.getContent());
    }

    @Override
    public void updateDisplay() {

    }
}
