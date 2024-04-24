package net.jchad.server.controller;

import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.server.Server;

// Used for controlling the server from the view
public class
ServerController {
    private final Server server;

    public ServerController(MessageHandler messageHandler) {
        server = new Server(messageHandler);
    }

    public void startServer() {
        server.runServer();
    }

    public void sendCommand(String command) {
        // Send command to server here
    }
}
