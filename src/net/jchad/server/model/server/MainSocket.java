package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Runs the main socket and manages all the other socket threads
 */
public final class MainSocket implements Runnable{
    private final MessageHandler messageHandler;
    private final int port;
    private ExecutorService executor;
    private final Server server;


    protected MainSocket(int port, Server server) {
        if (server == null) throw new IllegalArgumentException("Server can't be null");
        this.server = server;
        if (server.getMessageHandler() == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = server.getMessageHandler();
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
                socket.setSoTimeout(1000 * 1000); //TODO Use config values for the timeout

                messageHandler.handleInfo("Socket connected: " + socket);
                // Same as "new ServerThread(socket, handler).start();
                executor.submit(new ServerThread(socket,  server));


            }

        } catch (SocketTimeoutException ste) {
            messageHandler.handleError(ste);
        } catch (Exception e) {
            messageHandler.handleFatalError(new Exception("An unknown error occurred", e));
        } finally {
            executor.shutdownNow();

        }
    }


    public static <T> T getValueOrDefault(T value, Predicate<T> predicate, T defaultValue) {
        return predicate.test(value) ? value : defaultValue;
    }
}
