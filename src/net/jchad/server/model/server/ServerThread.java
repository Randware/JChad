package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class ServerThread implements Runnable{
    private final MessageHandler messageHandler;
    private final static List<ServerThread> serverThreadList = new ArrayList<>();
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    private final Socket socket;
    private String remoteAddress = "Unknown"; //The ip address of the client

    public ServerThread(Socket socket, MessageHandler messageHandler) throws IOException {
        if (messageHandler == null) {throw new IllegalArgumentException("Could not connect to [%s]: The MessageHandler is null".formatted(remoteAddress));}
        this.messageHandler = messageHandler;
        if (socket == null) {messageHandler.handleError(new IllegalArgumentException("Could not connect to [%s]: The Socket is null".formatted(remoteAddress)));  close();}
        this.socket = socket;
        this.remoteAddress = socket.getRemoteSocketAddress().toString();
        if (socket.getOutputStream() == null) {
            messageHandler.handleError(new NullPointerException("Could not connect to [%s]: The OutputStream is null".formatted(remoteAddress)));
            close();
            }
        printWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        listOperation(list -> list.add(this));
        String message = "";
        try {
            // If the client closes the connection the bufferedReader
            while ((message = bufferedReader.readLine()) != null) {
                messageHandler.handleInfo("Message from " + socket + ": " + message);
                printWriter.println("Message received");
                printWriter.flush();
            }
        } catch (IOException  e) {
           messageHandler.handleError(new IOException("Could not connect to [%s]: An unknown error occurred".formatted(remoteAddress),e));
        } finally {
            close();
        }

    }



    /**
     * Closes all fields
     * @throws IOException If an I/O error occurs. Should be caught by the MainSocket
     */
    public void close() {
        try {
            listOperation(list -> list.remove(this));
            if (printWriter != null) printWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            messageHandler.handleError(new IOException("Info while closing connection to [%s]: An unknown error occurred".formatted(remoteAddress),e));
        } finally {
            Thread.currentThread().interrupt();
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
     * Without this methode problems could arise when multiple threads add or remove themselves from the array list
     * due to the nature of the ArrayList (Because it isn't concurrency safe.)
     * @param statement The statement that gets executed on the arraylist
     * @return The return value of the operation
     *
     */
    public  static synchronized <R> R listOperation(Function<List<ServerThread>, R> statement) {
        return statement.apply(serverThreadList);
    }
}
