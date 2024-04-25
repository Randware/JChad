package net.jchad.server.model.server;

import net.jchad.server.model.config.*;
import net.jchad.server.model.config.store.Config;
import net.jchad.server.model.config.store.serverSettings.ServerSettings;
import net.jchad.server.model.error.MessageHandler;

// Contains all necessary server data
public class Server implements ConfigObserver {
    private final MessageHandler messageHandler;
    private ConfigManager configManager;

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


        mainSocket = new MainSocket(config.getServerSettings().getPort(), messageHandler);
        new Thread(mainSocket).start();
        messageHandler.handleInfo("Server started on port " + config.getServerSettings().getPort());
    }

    @Override
    public void configUpdated() {
        config = configManager.getConfig();

        messageHandler.handleInfo("Updated server configuration");
    }
}
