package net.jchad.server.model.server;

import net.jchad.server.model.config.*;
import net.jchad.server.model.config.store.Config;
import net.jchad.server.model.error.MessageHandler;

// Contains all necessary server data
public class Server implements ConfigObserver {
    private final MessageHandler messageHandler;
    private ConfigManager configManager;
    private final double version = 1.00;

    private MainSocket mainSocket;
    private Config config;

    public Server(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void runServer() {
        messageHandler.handleInfo("Starting server ...");

        this.configManager = new ConfigManager(messageHandler, this);
        this.config = configManager.getConfig();
        messageHandler.handleInfo("Loaded server config");


        mainSocket = new MainSocket(config.getServerSettings().getPort(), this);
        new Thread(mainSocket).start();
        messageHandler.handleInfo("Server started on port " + config.getServerSettings().getPort());
    }

    private void executeCommand(String command) {

    }

    @Override
    public void configUpdated() {
        config = configManager.getConfig();

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
