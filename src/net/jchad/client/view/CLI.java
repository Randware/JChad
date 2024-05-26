package net.jchad.client.view;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.client.model.store.connection.ConnectionDetailsBuilder;
import net.jchad.shared.io.terminal.Terminal;
import net.jchad.shared.io.terminal.UserExitedException;
import org.fusesource.jansi.AnsiConsole;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

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
        AnsiConsole.systemInstall();

        mainScreen();
    }

    private void mainScreen() {
        while (true) {
            emptyLine();
            terminal.outputMessage(ansi().fg(RED).a("Welcome to JChad!").reset().toString());
            emptyLine();

            terminal.outputMessage("Select an operation");
            emptyLine();
            terminal.outputMessage("Connections:");
            terminal.outputMessage("\"add\", \"view\", \"connect\"");
            emptyLine();
            terminal.outputMessage("Other:");
            terminal.outputMessage("\"about\",\"exit\"");
            emptyLine();

            String input = readString(true);

            if (!parseOperation(input)) {
                emptyLine();
                terminal.outputMessage("This is not an available option");
            }
        }
    }

    private boolean parseOperation(String operation) {
        switch (operation.toLowerCase()) {
            case "add" -> {
                emptyLine();
                terminal.outputMessage(ansi().fg(RED).a("Add new connection").reset().toString());
                emptyLine();

                client.configuration().addConnection(enterConnectionScreen(true));
            }

            case "view" -> {
                displayConnections();

                while (true) {
                    emptyLine();
                    terminal.outputMessage("Enter the index of a connection to connect, enter \"exit\" to go back.");
                    String input = readString(true);

                    if (input.equals("exit")) {
                        break;
                    }

                    try {
                        int index = Integer.parseInt(input);
                        ConnectionDetails connection = client.configuration().getConnections().get(index);

                        connectScreen(connection);
                        break;
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        emptyLine();
                        terminal.outputMessage("Please enter a valid option");
                    }
                }
            }

            case "connect" -> {
                emptyLine();
                terminal.outputMessage(ansi().fg(RED).a("Connect to a server").reset().toString());
                emptyLine();

                connectScreen(enterConnectionScreen(false));
            }

            case "about" -> {
                emptyLine();
                terminal.outputMessage("You are using the official JChad client!");
            }

            case "exit" -> {
                exit();
            }

            default -> {
                return false;
            }
        }

        return true;
    }

    private void displayConnections() {
        ArrayList<ConnectionDetails> connections = client.configuration().getConnections();

        emptyLine();
        terminal.outputMessage(ansi().fg(RED).a("Saved connections").reset().toString());

        for (int x = 0; x < connections.size(); x++) {
            ConnectionDetails connection = connections.get(x);

            emptyLine();
            terminal.outputMessage("[%s] %s".formatted(x, connection.getConnectionName()));
            terminal.outputMessage(" ├ %s:%s".formatted(connection.getHost(), connection.getPort()));
            terminal.outputMessage(" └ %s".formatted(connection.getUsername()));
        }
    }

    private ConnectionDetails enterConnectionScreen(boolean save) {
        ConnectionDetailsBuilder builder = new ConnectionDetailsBuilder();

        if (save) {
            while (true) {
                terminal.outputMessage("Give this connection a name");
                String connectionName = readString(true);
                emptyLine();

                if (!client.configuration().connectionExists(connectionName)) {
                    builder.addConnectionName(connectionName);
                    break;
                } else {
                    terminal.outputMessage("Name already in use");
                }
            }
        }

        terminal.outputMessage("Please enter the host of the connection");
        builder.addHost(readString(true));

        emptyLine();
        terminal.outputMessage("Please enter the port of the connection");
        builder.addPort(readInt());

        emptyLine();
        terminal.outputMessage("Please set a username");
        builder.addUsername(readString(true));

        emptyLine();
        terminal.outputMessage("Please enter the password for the server (can be left blank)");
        builder.addPassword(readString(false));

        return builder.build();
    }

    private void connectScreen(ConnectionDetails connection) {
        client.connect(connection);
        isConnected = true;
        chatsScreen(true);
        chattingScreen();
    }

    private void disconnectScreen() {
        client.disconnect();
        isConnected = false;

        emptyLine();
        terminal.outputMessage(ansi().fg(RED).boldOff().a("Disconnected from the current connection").reset().toString());
    }

    private void chattingScreen() {
        if (isConnected) {
            emptyLine();
            terminal.outputMessage(
                    ansi().fg(RED)
                            .a("You can now chat. Enter \"/chats\" to view and change chats, \"/exit\" to disconnect.")
                            .reset()
                            .toString()

            );
            emptyLine();
        }

        while (isConnected) {
            String userInput = readString(true);

            if (userInput.equals("/chats")) {
                chatsScreen(false);
            } else if (userInput.equals("/exit")) {
                disconnectScreen();
                break;
            } else {
                emptyLine();
                client.sendMessage(userInput);
            }
        }
    }

    private void chatsScreen(boolean firstSelect) {
        while (true) {
            terminal.outputMessage(ansi().fg(RED).a("\nAvailable chats").reset().toString());

            client.getChats().forEach(chat -> {
                terminal.outputMessage("\n\"%s\"".formatted(chat.getName()));
            });

            emptyLine();

            if (firstSelect) {
                terminal.outputMessage("Please enter the chat you want to join, or \"exit\" to disconnect");
            } else {
                terminal.outputMessage("Please enter the chat you want to join, or \"exit\" to go back");
            }


            String input = readString(true);

            if (input.equals("exit")) {
                if (firstSelect) {
                    disconnectScreen();
                }

                break;
            }

            ClientChat chat = client.getChat(input);

            if (chat == null) {
                emptyLine();
                terminal.outputMessage("This is not a valid chat");
            } else {
                client.setCurrentChat(chat);
                break;
            }
        }
    }

    private void exit() {
        emptyLine();
        terminal.outputMessage(ansi().fg(RED).a("Exiting the client").reset().toString());
        emptyLine();

        client.disconnect();
        terminal.close();

        System.exit(0);
    }

    private void emptyLine() {
        terminal.outputMessage("");
    }

    private String readString(boolean ensureNonEmpty) {
        try {
            while (true) {
                String input = terminal.read();

                if (!input.isBlank() || !ensureNonEmpty) {
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
            while (true) {
                String input = terminal.read();

                if (!input.isBlank()) {
                    try {
                        return Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        emptyLine();
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
        terminal.outputMessage(ansi().fg(RED).boldOff().a("[Fatal Error] ").reset() + e.getMessage());

        disconnectScreen();

        isConnected = false;
    }

    @Override
    public void handleError(Exception e) {
        terminal.outputMessage(ansi().fg(RED).a("[Error] ").reset() + e.getMessage());
    }

    @Override
    public void handleWarning(String warning) {
        terminal.outputMessage(ansi().fg(YELLOW).a("[Warning] ").reset() + warning);
    }

    @Override
    public void handleInfo(String info) {
        terminal.outputMessage("[Info] " + info);
    }

    @Override
    public String displayPrompt(String promptTitle, String promptMessage) {
        terminal.outputMessage(ansi().bold().a("- %s -".formatted(promptTitle)).reset().toString());
        terminal.outputMessage(promptMessage);

        return readString(true);
    }

    @Override
    public void displayOwnMessage(ClientChatMessage message) {
        terminal.outputMessage(ansi().fg(CYAN).a("\"%s\" in \"%s\"\n%s\n%s".formatted(
                        message.getUsername(),
                        message.getChat(),
                        message.getContent(),
                        message.getPrettyTimestamp()
                                .format(DateTimeFormatter.ofPattern("dd.M. - HH:mm"))
                )).reset().toString()
        );
    }

    @Override
    public void displayOtherMessage(ClientChatMessage message) {
        terminal.outputMessage("\"%s\" in \"%s\"\n%s\n%s".formatted(
                        message.getUsername(),
                        message.getChat(),
                        message.getContent(),
                        message.getPrettyTimestamp()
                                .format(DateTimeFormatter.ofPattern("dd.M. - HH:mm"))
                )
        );
        emptyLine();
    }

    @Override
    public void updateDisplay() {
        // Nothing to do here, because we access the chats every time we want to show them, therefore
        // we don't have to update any view.
    }
}
