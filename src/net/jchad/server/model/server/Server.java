package net.jchad.server.model.server;

import net.jchad.server.model.config.Config;
import net.jchad.server.model.config.ConfigManagerRewrite;
import net.jchad.server.model.config.ConfigObserver;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.config.ConfigManager;

import java.io.IOException;

// Contains all necessary server data
public class Server implements ConfigObserver {
    private final MessageHandler messageHandler;
    private final ConfigManagerRewrite configManager;

    private MainSocket mainSocket;
    private Config config;

    public Server(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.configManager = new ConfigManagerRewrite(messageHandler, this);
    }

    public void runServer() {
        messageHandler.handleInfo("Starting server ...");

        this.config = configManager.getConfig();
        messageHandler.handleInfo("Loaded server config");


        mainSocket = new MainSocket(config.getPort(), messageHandler);
        new Thread(mainSocket).start();
        messageHandler.handleInfo("Server started on port " + config.getPort());
    }

    @Override
    public void configUpdated() {
        config = configManager.getConfig();

        messageHandler.handleInfo("Updated server config");
    }
}
