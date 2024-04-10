package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread{
    private final MessageHandler messageHandler;
    private final static List<ServerThread> serverThreadList = new ArrayList<>();
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    private final Socket socket;

    public ServerThread(Socket socket, MessageHandler messageHandler) throws IOException {
        if (messageHandler == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = messageHandler;
        if (socket == null) messageHandler.handleError(new IllegalArgumentException("MessageHandler can't be null"));
        this.socket = socket;
        printWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        serverThreadList.add(this);
        String message = "";
        try {
            while ((message = bufferedReader.readLine()) != null) {
                messageHandler.handleInfo("Message from " + socket + ": " + message);
                printWriter.println("Message received");
                printWriter.flush();
            }
        } catch (IOException e) {
            messageHandler.handleError(new UncheckedIOException("Connection couldn't be established with %s because of an unexpected error".formatted(socket.getRemoteSocketAddress()),e));
        }

    }



    /**
     * Closes all fields
     * @throws IOException If an I/O error occurs. Should be catched by the MainSocket
     */
    public void close() {
        try {
            serverThreadList.remove(this);
            if (printWriter != null) printWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            messageHandler.handleError(new UncheckedIOException("An unexpected I/O-Exception occurred",e));
        }
    }
}
