package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

// Contains all necessary server data
public class Server {
    private final MessageHandler messageHandler;

    public Server(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void runServer() {
        // Run server ...
    }
}
