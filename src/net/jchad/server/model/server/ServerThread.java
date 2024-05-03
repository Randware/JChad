package net.jchad.server.model.server;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.jchad.server.model.config.store.Config;
import net.jchad.server.model.config.store.internalSettings.DefaultInternalSettings;
import net.jchad.server.model.error.MessageHandler;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ServerThread implements Runnable{
    private final Server server;
    private final MessageHandler messageHandler;
    private final static List<ServerThread> serverThreadList = new ArrayList<>();
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    private final JsonReader jsonReader;
    private final JsonWriter jsonWriter;
    private final Socket socket;
    private final InetSocketAddress inetAddress;
    private String remoteAddress = "Unknown"; //The ip address of the client

    public ServerThread(Socket socket, Server server) throws IOException {
        if (server == null) throw new IllegalArgumentException("Could not connect to [%s]: The MessageHandler is null".formatted(remoteAddress));
        this.server = server;
        if (server.getMessageHandler() == null) {throw new IllegalArgumentException("Could not connect to [%s]: The MessageHandler is null".formatted(remoteAddress));}
        this.messageHandler = server.getMessageHandler();
        if (socket == null) {
            messageHandler.handleError(new IllegalArgumentException("Could not connect to [%s]: The Socket is null".formatted(remoteAddress)));
            close("The provided socket is null!");
        }
        this.socket = socket;
        this.inetAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        this.remoteAddress = inetAddress.toString();


        if (socket.getOutputStream() == null) {
            messageHandler.handleError(new NullPointerException("Could not connect to [%s]: The OutputStream is null".formatted(remoteAddress)));
            close("The output stream of the socket is null!");
            }
        printWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        jsonReader = new JsonReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        jsonReader.setLenient(true);
        jsonWriter = new JsonWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
    }

    public void run() {
        listOperation(list -> list.add(this));
        try {



        }  catch (Exception e) {
            messageHandler.handleError(new Exception("An error occurred while connected to [%s]: %s".formatted(remoteAddress,e.getMessage()), e));
        } finally {
            close();
        }
    }


    /**
     * Closes all streams in the {@link net.jchad.server.model.server.ServerThread ServerThread} safely
     */
    public void close() {
        close(null);
    }

    /**
     * Closes all streams in the {@link net.jchad.server.model.server.ServerThread ServerThread} safely
     * @param reason The reason why the connection gets closed. <b>May be null!</b>
     *               The reason gets "skipped" if it is null!
     * @throws IOException If an I/O error occurs. Should be caught by the MainSocket
     */
    public void close(String reason) {
        if (Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            listOperation(list -> list.remove(this));
            if (jsonWriter != null) {
                jsonWriter.beginObject(); //This prevents an exception (Incomplete document)
                jsonWriter.endObject(); //An empty stream counts as incomplete document. TO prevent this, this gets added to the stream
                jsonWriter.flush();
                jsonWriter.close();
            }
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (jsonReader != null) {
                jsonReader.close();
            }
            if (socket != null) socket.close();
        } catch (IOException e) {
            messageHandler.handleError(new IOException("Info while closing connection to [%s]: An unknown error occurred".formatted(remoteAddress),e));
        } finally {
            messageHandler.handleInfo("Closing connection with " + remoteAddress + ((reason == null) ? "" : (" Reason: " + reason)));
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

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public JsonReader getJsonReader() {
        return jsonReader;
    }

    public JsonWriter getJsonWriter() {
        return jsonWriter;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public InetSocketAddress getInetAddress() {
        return inetAddress;
    }

    public Socket getSocket() {
        return socket;
    }
    public Config getConfig() {
        return server.getConfig();
    }

    /** //TODO I am unsure if this is a permanent solution. Checking if the config values are correct should not be done in the ServerThread.
     * Returns a value from the config or the default one if the user set value is invalid
     * @return
     */
    public long getConnectionRefreshIntervalMillis() {
        if (server.getConfig().getInternalSettings().getConnectionRefreshIntervalMillis() < 0) return DefaultInternalSettings.get().getConnectionRefreshIntervalMillis();
        else return server.getConfig().getInternalSettings().getConnectionRefreshIntervalMillis();
    }

    public int  getRetriesOnMalformedJSONduringVersioning() {
        if (server.getConfig().getInternalSettings().getRetriesOnMalformedJSON() <= 0) return DefaultInternalSettings.get().getRetriesOnMalformedJSON();
        else return server.getConfig().getInternalSettings().getRetriesOnMalformedJSON();
    }


}
