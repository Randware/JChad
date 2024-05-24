package net.jchad.client.view;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.client.model.store.connection.ConnectionDetailsBuilder;
import net.jchad.shared.io.terminal.Terminal;
import net.jchad.shared.io.terminal.UserExitedException;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.util.ArrayList;

/**
 * Responsible for displaying the client output in CLI mode.
 * This class is only really used in the development process and
 * for demonstration purposes.
 */
public class CLI implements ViewCallback {
    private ClientController client;
    private Terminal terminal;

    private boolean isConnected;

    public static void main(String[] args) {
        new CLI().runCLI();
    }

    private void runCLI() {
        client = new ClientController(this);
        terminal = Terminal.get();
        isConnected = false;

        connectScreen();
    }

    private void connectScreen() {
        terminal.outputMessage("Welcome to JChad!");

        ConnectionDetailsBuilder builder = new ConnectionDetailsBuilder();

        terminal.outputMessage("Please enter the host of the connection");
        builder.addHost(readString(true));

        terminal.outputMessage("Please enter the port of the connection");
        builder.addPort(readInt());

        terminal.outputMessage("Please set a username");
        builder.addUsername(readString(true));

        terminal.outputMessage("Please enter the password for the server (can be left blank)");
        builder.addPassword(readString(false));

        client.connect(builder.build());
        terminal.outputMessage("Successfully connected to server");
        isConnected = true;
        mainScreen();
    }

    private void mainScreen() {
        terminal.outputMessage("You can now chat. Enter \"/chats\" to view and change chats.");

        while(isConnected) {
            String userInput = readString(true);

            if(userInput.equals("/chats")) {
                chatsScreen();
            } else {
                client.sendMessage(userInput);
            }
        }
    }

    private void chatsScreen() {
        while(true) {
            terminal.outputMessage("Available chats:");

            client.getChats().forEach(chat -> {
                terminal.outputMessage("\"%s\"".formatted(chat.getName()));
            });

            terminal.outputMessage("Please enter the chat you want to join, or \"exit\" to go back");

            String input = readString(true);

            if(input.equals("exit")) {
                break;
            }

            ClientChat chat = client.getChat(input);

            if(chat == null) {
                terminal.outputMessage("This is not a valid chat");
            } else {
                client.setCurrentChat(chat);
                break;
            }
        }
    }

    private void exit() {
        client.disconnect();
        terminal.close();

        System.exit(0);
    }

    private String readString(boolean ensureNonEmpty) {
        try {
            while(true) {
                String input = terminal.read();

                if(!input.isBlank() || !ensureNonEmpty) {
                    return input;
                }
            }
        } catch (UserExitedException e) {
            exit();
            return null;
        }
    }

    private int readInt() {
        try {
            while(true) {
                String input = terminal.read();

                if(!input.isBlank()) {
                    try {
                        return Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        terminal.outputMessage("This is not a valid number");
                    }
                }
            }
        } catch (UserExitedException e) {
            exit();
            return 0;
        }
    }

    @Override
    public void handleFatalError(Exception e) {
        terminal.outputMessage("[Fatal error] " + e.getMessage());
        terminal.outputMessage("Disconnected from the current connection");
        e.printStackTrace();
        isConnected = false;
        connectScreen();
    }

    @Override
    public void handleError(Exception e) {
        terminal.outputMessage("[Error] " + e.getMessage());
    }

    @Override
    public void handleWarning(String warning) {
        terminal.outputMessage("[Warning] " + warning);
    }

    @Override
    public void handleInfo(String info) {
        terminal.outputMessage("[Info] " + info);
    }

    @Override
    public String displayPrompt(String promptTitle, String promptMessage) {
        terminal.outputMessage("- %s -".formatted(promptTitle));
        terminal.outputMessage(promptMessage);

        return readString(true);
    }

    @Override
    public void displayOwnMessage(ClientChatMessage message) {
        terminal.outputMessage("%s\n%s\n%s\n%s".formatted(message.getChat(), message.getUsername(), message.getContent(), message.getTimestamp()));
    }

    @Override
    public void displayOtherMessage(ClientChatMessage message) {
        terminal.outputMessage("%s\n%s\n%s\n%s".formatted(message.getChat(), message.getUsername(), message.getContent(), message.getTimestamp()));
    }

    @Override
    public void updateDisplay() {
        // Nothing to do here, because we access the chats every time we want to show them, therefore
        // we don't have to update any view.
    }
}
