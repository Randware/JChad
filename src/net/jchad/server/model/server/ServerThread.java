package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ServerThread extends Thread{
    private final MessageHandler messageHandler;
    private final static List<ServerThread> serverThreadList = new ArrayList<>();
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    private final Socket socket;

    public ServerThread(Socket socket, MessageHandler messageHandler) throws IOException {
        if (messageHandler == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = messageHandler;
        if (socket == null) messageHandler.handleError(new IllegalArgumentException("Socket can't be null"));
        this.socket = socket;
        printWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        String message = "";
        try {
            while ((message = bufferedReader.readLine()) != null) {
                messageHandler.handleInfo("Message from " + socket + ": " + message);
                printWriter.println("Message received");
                printWriter.flush();
            }
            if (socket.isClosed()) close();
        } catch (IOException  e) {
           messageHandler.handleError(new UncheckedIOException("Connection couldn't be established with %s because of an unexpected error".formatted(socket.getRemoteSocketAddress()),e));
           close();
        }

    }



    /**
     * Closes all fields
     * @throws IOException If an I/O error occurs. Should be catched by the MainSocket
     */
    public void close() {
        try {
            listOperation(list -> list.remove(this));
            if (printWriter != null) printWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            messageHandler.handleError(new UncheckedIOException("An unexpected I/O-Exception occurred",e));
        }
    }

    /**
     * Returns a List of all current connected ServerThreads IMMUTABLE BY DEFAULT
     * @return An immutable List
     */
    public static List<ServerThread> getServerThreadList() {
        return getServerThreadList(false);
    }

    /**
     * Returns a List of all current connected ServerThreads
     * @param mutable If the list that gets returned is mutable or not
     * @return The ServerThreadList
     */
    public static List<ServerThread> getServerThreadList(boolean mutable) {
        return mutable ? serverThreadList : List.copyOf(serverThreadList);
    }

    /**
     * This ensures that all operations done to the list get executed correctly.
     * Without this methode problems could arise when multiple threads add or remove  themselves from the array list
     * due to the nature of the ArrayList (Because it isn't concurrency safe!)
     * @param statement The statement that gets executed on the arraylist
     */
    public static synchronized void listOperation(Consumer<List<ServerThread>> statement) {
        statement.accept(serverThreadList);
    }
}
