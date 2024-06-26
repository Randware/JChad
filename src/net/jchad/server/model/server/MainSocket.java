package net.jchad.server.model.server;

import com.google.gson.Gson;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.server.util.HandshakeTimeoutChecker;
import net.jchad.shared.networking.ip.IPAddress;
import net.jchad.shared.networking.ip.InvalidIPAddressException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs the main socket and manages all the other socket threads
 */
public final class MainSocket implements Runnable{
    private final MessageHandler messageHandler;
    private final int port;
    private ServerSocket serverSocket = null;
    private final Gson gson = new Gson();
    private ExecutorService executor;
    private final Server server;
    private final Set<ServerThread> serverThreadSet = ConcurrentHashMap.newKeySet();
    private boolean running = true;
    private Thread connectionCheckerThread = null;


    protected MainSocket(int port, Server server) {
        if (server == null) throw new IllegalArgumentException("Server can't be null");
        this.server = server;
        if (server.getMessageHandler() == null) throw new IllegalArgumentException("MessageHandler can't be null");
        this.messageHandler = server.getMessageHandler();
        if (port < 1 || port > 65535) messageHandler.handleFatalError(new IllegalArgumentException("Allowed Server-Port range is between 0 and 65536"));
        if (1024 > port || port > 49151) messageHandler.handleWarning("Server-Port is outside of the User ports! Refer to https://en.wikipedia.org/wiki/Registered_port");
        this.port = port;
        executor = Executors.newVirtualThreadPerTaskExecutor();
        if (server.getConfig().getInternalSettings().isEnableHandshakeTimeoutThread()) {
            connectionCheckerThread = new Thread(new HandshakeTimeoutChecker(this));
            connectionCheckerThread.start();
            messageHandler.handleInfo("The Handshake Timeout Checker Thread was enabled");
        }
    }


    @Override
    public void run() {
        try {

            serverSocket = new ServerSocket(port);
            while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();




                executor.submit(new ServerThread(socket,  this));

            }
        } catch(SocketException se) {
            if (!running) {

                //Ignores the exception if it gets throw, when the server shutdowns.
                //When I close the serverSocket while the serverSocket.accept()
                // methode is running a guaranteed Exception gets thrown,
                // therefore, we ignore ethe exception
            } else {
                messageHandler.handleFatalError(new Exception("An unknown error occurred", se));
            }
        } catch (Exception e) {
            messageHandler.handleFatalError(new Exception("An unknown error occurred", e));
        } finally {
            executor.shutdownNow();
            closeServerSocket();
        }
    }

    public void shutdown() {
        shutdown("The server is shutting down");
    }

    public void shutdown(String message) {
        running = false;
        messageHandler.handleDebug("Closing all connections before shutting the MainSocket down");
        for (ServerThread element : serverThreadSet) {
            messageHandler.handleDebug("Trying to close connection with " + element.getRemoteAddress());
            element.close(message, false);
            messageHandler.handleDebug("Connection closed successfully with " + element.getRemoteAddress() + " !");
        }

        executor.shutdownNow();
        closeServerSocket();

    }


    /**
     * This closes the ServerSocket if it is not null.
     */
    public void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                messageHandler.handleFatalError(new IOException("Error while closing the MainSocket.", e));
            }
        }
    }


    public boolean isBanned(InetSocketAddress inetSocketAddress) {
        try {
            String formatedInetAddress = inetSocketAddress.toString().replace("/", "");
            if (server.getConfig().getServerSettings().isBlacklist() && server.getConfig().getServerSettings().getBlacklistedIPs().contains(IPAddress.fromString(formatedInetAddress.substring(0, formatedInetAddress.lastIndexOf(":"))))) {
                return true;

            }
        } catch (InvalidIPAddressException e) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the ip address is allowed to join
     * @return When the whitelist is true and the ip of the client is in the allowed IPs, this returns true
     */
    public boolean isWhitelisted(InetSocketAddress inetSocketAddress) {
        try {
            String formatedInetAddress = inetSocketAddress.toString().replace("/", "");
            if (server.getConfig().getServerSettings().isWhitelist() && server.getConfig().getServerSettings().getWhitelistedIPs().contains(IPAddress.fromString(formatedInetAddress.substring(0, formatedInetAddress.lastIndexOf(":"))))) {
                return true;
            } else return !server.getConfig().getServerSettings().isWhitelist();
        } catch (InvalidIPAddressException ignored) {}
        return false;
    }


    /**
     * This methode gets called, by the {@link Server} if one of the ServerConfigs gets updated.
     */
    public void configUpdated() {
        if (server.getConfig().getInternalSettings().isEnableHandshakeTimeoutThread() && connectionCheckerThread == null) {
            connectionCheckerThread = new Thread(new HandshakeTimeoutChecker(this));
            connectionCheckerThread.start();
            messageHandler.handleInfo("The Handshake Timeout Checker Thread was enabled");
        } else if (!server.getConfig().getInternalSettings().isEnableHandshakeTimeoutThread() && connectionCheckerThread != null) {
            connectionCheckerThread.interrupt();
            messageHandler.handleInfo("The Handshake Timeout Checker Thread was disabled");
            connectionCheckerThread = null;
        }

        String newConf = Server.getCurrentServerInfo(server).toJSON();
        serverThreadSet.forEach(serverThread -> {
            if ( serverThread.getUser() != null && serverThread.getUser().isReadyToReceiveMessages()) {
                serverThread.write(newConf);
            }
        });
    }

    /**
     * Returns a Set of all current connected ServerThreads.
     * <b>This returns a copy of the Set! The {@link net.jchad.server.model.server.ServerThread ServerThreads} are still mutable</b>
     * <p>If the original set gets modified the returned set stays the same!</p>
     * Use {@link MainSocket#getServerThreadSet(boolean)}
     * @return A copied set of the {@link MainSocket#serverThreadSet}
     */
    public Set<ServerThread> getServerThreadSet() {
        return getServerThreadSet(false);
    }

    /**
     * Returns a Set of all current connected ServerThreads
     * @param createCopyOfSet <p>Set this to true if the returning {@link MainSocket#serverThreadSet} should be a copy of the original one.</p>
     *                        <p>Otherwise set this to false to get the mutable {@link MainSocket#serverThreadSet}</p>
     * @return The {@link MainSocket#serverThreadSet}
     */
    public Set<ServerThread> getServerThreadSet(boolean createCopyOfSet) {
        if (createCopyOfSet) {
            Set<ServerThread> returnedSet = ConcurrentHashMap.newKeySet();
            returnedSet.addAll(serverThreadSet);
            return returnedSet;
        } else {
            return serverThreadSet;
        }
    }

    public Server getServer() {
        return server;
    }

    public Gson getGson() {
        return gson;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }


}
