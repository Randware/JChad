package net.jchad.server.model.server;

import com.google.gson.Gson;
import net.jchad.server.model.config.store.Config;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.server.util.MainHelperThread;
import net.jchad.server.model.server.util.PasswordHelperThread;
import net.jchad.server.model.server.util.UsernameHelperThread;
import net.jchad.server.model.users.User;
import net.jchad.shared.networking.packets.defaults.BannedPacket;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;
import net.jchad.shared.networking.packets.defaults.NotWhitelistedPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationPacket;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread implements Runnable{
    private final Server server;
    private final MainSocket mainSocket;
    private final MessageHandler messageHandler;
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    private final Scanner scanner;
    private final Socket socket;
    private final InetSocketAddress inetAddress;
    private String remoteAddress = "Unknown"; //The ip address of the client
    private User user = null;
    private final Gson gson = new Gson();

    public ServerThread(Socket socket, MainSocket mainSocket) throws IOException {
        if (mainSocket == null) throw new IllegalArgumentException("Could not connect to [%s]: The MainServer is null".formatted(remoteAddress));
        this.mainSocket = mainSocket;
        this.server = mainSocket.getServer();
        if (server.getMessageHandler() == null) {throw new IllegalArgumentException("Could not connect to [%s]: The MessageHandler is null".formatted(remoteAddress));}
        this.messageHandler = server.getMessageHandler();
        if (socket == null) {
            messageHandler.handleError(new IllegalArgumentException("Could not connect to [%s]: The Socket is null".formatted(remoteAddress)));
            close("The provided socket is null!");
        }
        this.socket = socket;
        this.inetAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        this.remoteAddress = inetAddress.toString().replace("/", "");


        if (socket.getOutputStream() == null) {
            messageHandler.handleError(new NullPointerException("Could not connect to [%s]: The OutputStream is null".formatted(remoteAddress)));
            close("The output stream of the socket is null!");
            }
        printWriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        scanner = new Scanner(socket.getInputStream());
    }

    public void run() {
        messageHandler.handleInfo(remoteAddress + " tries to establish a connection to the Server");
        mainSocket.getServerThreadSet(false).add(this);
        try {
        if (mainSocket.isBanned(inetAddress)) {
            printWriter.println(new BannedPacket().toJSON());
            printWriter.flush();
            close(remoteAddress + " is banned on this server");
            return;
        }

        if (!mainSocket.isWhitelisted(inetAddress)) {
            printWriter.println(new NotWhitelistedPacket().toJSON());
            printWriter.flush();
            close(remoteAddress + " is not whitelisted on this server");
            return;
        }

        //Send the server information to the client:
            printWriter.println(ServerInformationPacket.getCurrentServerInfo(server).toJSON());
            printWriter.flush();

        //Forces the client to provide the correct password
            if (server.getConfig().getServerSettings().isRequiresPassword()) {
                new PasswordHelperThread(this).getPassword();
            }

        //Come to an agreement for the client's username
            user = new UsernameHelperThread(this).arrangeUser();

            messageHandler.handleInfo(remoteAddress + " connected successfully to the Server");


        //Initialization is finished. Client can chat now
            user.addJoinedChats("test"); //temporary
            user.setReadyToReceiveMessages(true);
            new MainHelperThread(this).start();
        } catch (ConnectionClosedException e) {
            //ignore
            /*
            You probably wonder why I throw this exception in the close() methode and catch it to just ignore it.
            Here is why:
            When you interrupt they sometimes continue runtime, therefore I throw this exception the stop the entire run methode
             */
        }
        catch (Exception e) {
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
        printWriter.println(new ConnectionClosedPacket(reason).toJSON());//Sends the client a notification that the connection gets closed
        printWriter.flush();
        try {
            mainSocket.getServerThreadSet(false).remove(this);
            if (user != null) {
                User.removeUser(this);
            }
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (scanner != null) {
                scanner.close();
            }
            if (socket != null) socket.close();
        } catch (IOException e) {
            messageHandler.handleError(new IOException("Info while closing connection to [%s]: An unknown error occurred".formatted(remoteAddress),e));
        } finally {
            messageHandler.handleInfo("Closing connection with " + remoteAddress + ((reason == null) ? "" : (" Reason: " + reason)));
            Thread.currentThread().interrupt();
            throw new ConnectionClosedException("The connection was closed.");
        }
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


    public Server getServer() {
        return server;
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

    public Scanner getScanner() {
        return scanner;
    }

    public User getUser() {
        return user;
    }

    public MainSocket getMainSocket() {
        return mainSocket;
    }

    public Gson getGson() {
        return gson;
    }
}
