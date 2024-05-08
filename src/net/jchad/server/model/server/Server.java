package net.jchad.server.model.server;

import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.chats.ChatManager;
import net.jchad.server.model.command.Command;
import net.jchad.server.model.config.*;
import net.jchad.server.model.config.store.Config;
import net.jchad.server.model.error.MessageHandler;

import java.util.ArrayList;

// Contains all necessary server data
public class Server implements ConfigObserver {
    private final MessageHandler messageHandler;
    private ConfigManager configManager;
    private ChatManager chatManager;
    private final double version = 1.00;

    private MainSocket mainSocket;
    private Config config;
    private ArrayList<Chat> chats;

    public Server(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void runServer() {
        messageHandler.handleInfo("Starting server ...");

        this.configManager = new ConfigManager(messageHandler, this);
        this.config = configManager.getConfig();
        messageHandler.handleInfo("Loaded server configuration");

        this.chatManager = new ChatManager(messageHandler, this);
        this.chats = chatManager.getAllChats();
        messageHandler.handleInfo("Loaded chat configuration");


        mainSocket = new MainSocket(config.getServerSettings().getPort(), this);
        new Thread(mainSocket).start();
        messageHandler.handleInfo("Server started on port " + config.getServerSettings().getPort());
    }

    public void stopServer() {
        messageHandler.handleInfo("Stopping server");

        // TODO: Implement proper server shutdown logic here instead
        System.exit(0);
    }

    public void executeCommand(String command) {
        Command.executeCommandString(command, this);
    }

    @Override
    public void configUpdated() {
        config = configManager.getConfig();
        chats = chatManager.getAllChats();

        messageHandler.handleInfo("Updated server configuration");
    }

    public Config getConfig() {
        return config;
    }

    public MainSocket getMainSocket() {
        return mainSocket;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
