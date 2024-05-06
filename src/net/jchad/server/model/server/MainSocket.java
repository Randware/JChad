package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;
import net.jchad.shared.cryptography.CrypterManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * Runs the main socket and manages all the other socket threads
 */
public final class MainSocket implements Runnable{
    private final MessageHandler messageHandler;
    private final int port;
    private ExecutorService executor;
    private final Server server;
    private final String base64AESkey;


    protected MainSocket(int port, Server server) {
        if (server == null) throw new IllegalArgumentException("Server can't be null");
        this.server = server;
        if (server.getMessageHandler() == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = server.getMessageHandler();
        if (port < 1 || port > 65535) messageHandler.handleFatalError(new IllegalArgumentException("Allowed Server-Port range is between 0 and 65536"));
        if (1024 > port || port > 49151) messageHandler.handleWarning("Server-Port is outside of the User ports! Refer to https://en.wikipedia.org/wiki/Registered_port");
        this.port = port;
        executor = Executors.newVirtualThreadPerTaskExecutor();
        base64AESkey = new CrypterManager().getAESKey();

    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {

                Socket socket = serverSocket.accept();

                executor.submit(new ServerThread(socket,  server));

            }

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
