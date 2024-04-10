package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Runs the main socket and manages all the other socket threads
 */
public final class MainSocket implements Runnable{
    private final MessageHandler messageHandler;
    private final int port;


    protected MainSocket(int port, MessageHandler messageHandler) {
        if (messageHandler == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = messageHandler;
        if (port < 1 || port > 65535) messageHandler.handleFatalError(new IllegalArgumentException("Allowed Server-Port range is between 0 and 65536"));
        if (1024 > port || port > 49151) messageHandler.handleWarning("Server-Port is outside of the User ports! Refer to https://en.wikipedia.org/wiki/Registered_port");
        this.port = port;
    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                messageHandler.handleInfo("Socket connected: " + socket);
                // Same as "new ServerThread(socket, handler).start();"
                ServerThread serverThread = new ServerThread(socket,  messageHandler);
                serverThread.start();
            }

        } catch (IOException e) {
            messageHandler.handleFatalError(new UncheckedIOException("An unknown I/O error occurred", e));
        }
    }
}
