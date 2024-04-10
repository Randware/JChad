package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.store.config.Config;
import net.jchad.server.model.store.config.DefaultConfig;

import java.net.Socket;

// Contains all necessary server data
public class Server {
    private final MessageHandler messageHandler;
    private Config config;
    private MainSocket mainSocket;

    public Server(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        config = DefaultConfig.get();
        mainSocket = new MainSocket(config.getPort(), messageHandler);
    }

    public void runServer() {
        new Thread(mainSocket).start();
        messageHandler.handleInfo("Server started on port " + config.getPort());
    }
}
