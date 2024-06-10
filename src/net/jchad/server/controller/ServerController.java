package net.jchad.server.controller;

import net.jchad.server.model.config.store.Config;
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
       server.executeCommand(command);
    }

    public void stopServer() {
        server.stopServer();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public Config getServerConfig() {
        return server.getConfig();
    }

    public Server getServer(){return server;}
}
