package net.jchad.server.model.server;

import net.jchad.server.model.config.Config;
import net.jchad.server.model.config.ConfigObserver;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.config.ConfigManager;

import java.io.IOException;

// Contains all necessary server data
public class Server implements ConfigObserver {
    private final MessageHandler messageHandler;
    private final ConfigManager configManager;

    private MainSocket mainSocket;
    private Config config;

    public Server(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.configManager = new ConfigManager(messageHandler, this);
    }

    public void runServer() {
        messageHandler.handleInfo("Starting server ...");

        try {
            this.config = configManager.getConfig();
            messageHandler.handleInfo("Successfully loaded config");
        } catch (IOException e) {
            messageHandler.handleFatalError(e);
        }

        mainSocket = new MainSocket(config.getPort(), messageHandler);
        new Thread(mainSocket).start();
        messageHandler.handleInfo("Server started on port " + config.getPort());
    }

    @Override
    public void configUpdated() {
        try {
            config = configManager.getConfig();

            messageHandler.handleInfo("Config changed, updating running config");
            System.out.println(config);
        } catch (IOException e) {
            messageHandler.handleWarning("Failed updating config, continuing with last working config");
        }
    }
}
