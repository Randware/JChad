package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Runs the main socket and manages all the other socket threads
 */
public final class MainSocket implements Runnable{
    private final MessageHandler messageHandler;
    private final int port;
    private ExecutorService executor;


    protected MainSocket(int port, MessageHandler messageHandler) {
        if (messageHandler == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = messageHandler;
        if (port < 1 || port > 65535) messageHandler.handleFatalError(new IllegalArgumentException("Allowed Server-Port range is between 0 and 65536"));
        if (1024 > port || port > 49151) messageHandler.handleWarning("Server-Port is outside of the User ports! Refer to https://en.wikipedia.org/wiki/Registered_port");
        this.port = port;
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {

                Socket socket = serverSocket.accept();
                messageHandler.handleInfo("Socket connected: " + socket);
                // Same as "new ServerThread(socket, handler).start();
                executor.execute(new ServerThread(socket,  messageHandler));

            }

        } catch (Exception e) {
            messageHandler.handleFatalError(new Exception("An unknown error occurred", e));
        } finally {
            executor.shutdownNow();

        }
    }
}
