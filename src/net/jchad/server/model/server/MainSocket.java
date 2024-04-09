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
    private final MessageHandler handler;
    private final int port;


    protected MainSocket(int port, MessageHandler handler) {
        if (handler == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.handler = handler;
        if (port < 1 || port > 65535) handler.handleFatalError(new IllegalArgumentException("Allowed Server-Port range is between 0 and 65536"));
        if (1024 > port || port > 49151) handler.handleWarning("Server-Port is outside of the User ports! Refer to https://en.wikipedia.org/wiki/Registered_port");
        this.port = port;
    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
                // Same as "new ServerThread(socket, handler).start();"
                ServerThread serverThread = new ServerThread(socket, handler);
                serverThread.start();


            }

        } catch (IOException e) {
            handler.handleFatalError(new UncheckedIOException("An unknown I/O error occurred", e));
        }
    }
}
