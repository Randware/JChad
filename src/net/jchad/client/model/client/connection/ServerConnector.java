package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.client.packets.PacketHandler;
import net.jchad.client.model.client.packets.PacketMapper;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.server.model.server.ConnectionClosedException;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.defaults.BannedPacket;
import net.jchad.shared.networking.packets.defaults.NotWhitelistedPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationResponsePacket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * This class is responsible for everything that needs to be done before the client
 * can successfully connect to the server. Examples of that may be connecting to the server,
 * exchanging encryption information and handling the password verification process.
 */
public class ServerConnector implements Callable<ServerConnection> {
    private static final Object lock = new Object();

    private volatile boolean isRunning;
    private boolean streamsTransfered;
    private final ConnectionDetails connectionDetails;
    private Client client;
    private ViewCallback viewCallback;

    /**
     * The Socket the connection will run on.
     */
    private Socket socket;

    /**
     * The {@link ConnectionWriter} which will be used to send data to the server.
     * This threads ownership will be moved to the {@link ServerConnection} once the connection
     * was successfully established.
     */
    private ConnectionWriter connectionWriter;

    /**
     * The {@link ConnectionReader} which will be used to receive data from the server.
     * This threads ownership will be moved to the {@link ServerConnection} once the connection
     * was successfully established.
     */
    private ConnectionReader connectionReader;

    public ServerConnector(Client client, ConnectionDetails connectionDetails) {
        if (client == null || connectionDetails == null) {
            throw new NullPointerException("The parameters can not be null");
        }
        this.client = client;
        this.viewCallback = client.getViewCallback();
        this.isRunning = false;
        this.connectionDetails = connectionDetails;
        streamsTransfered = false;
    }

    /**
     * Establish a connection to a server using the given {@link ConnectionDetails}.
     * Throws exception if something goes wrong during the connecting process and
     * a connection can't be established. Returns a valid {@link ServerConnection} if
     * a connection was successfully established.
     *
     * @param connectionDetails the {@link ConnectionDetails} which will be used to establish a connection.
     * @return a valid {@link ServerConnection} if a connection was successfully established.
     * @throws ClosedConnectionException if an error occurred during the connection process.
     */
    @Deprecated(forRemoval = true)
    public ServerConnection connect(ConnectionDetails connectionDetails) throws Exception {
        synchronized (lock) {
            if (isRunning) {
                shutdown();
            }
            isRunning = true;

        }
            return null;
    }

    @Override
    public ServerConnection call() throws IOException, ClosedConnectionException {
        if (isRunning) {
            shutdown();
        }

        isRunning = true;
        String host = connectionDetails.getHost();
        int port = connectionDetails.getPort();

        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            throw new ClosedConnectionException("Could not connect to host \"%s\" on port \"%s\"".formatted(host, port), e);
        }

        try {
            connectionWriter = new ConnectionWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new ClosedConnectionException("Could not open output and input streams for connection", e);
        }

        try {
            connectionReader = new ConnectionReader(socket.getInputStream());
            connectionReader.start();
        } catch (IOException e) {
            throw new ClosedConnectionException("Could not open output and input streams for connection", e);
        }
        ServerInformation serverInformation;
        try {
            Packet incomingPacket = readPacket();
            if (incomingPacket.getClass().equals(NotWhitelistedPacket.class)) {
                throw new ClosedConnectionException("Client is not whitelisted on this server");
            }
            if (incomingPacket.getClass().equals(BannedPacket.class)) {
                throw new ClosedConnectionException("Client is banned on this server");
            }
            if (incomingPacket.getClass().equals(ServerInformationResponsePacket.class)) {
                ServerInformationResponsePacket serverInfo = (ServerInformationResponsePacket) incomingPacket;
                serverInformation = new ServerInformation(
                        serverInfo.getServer_version(),
                        serverInfo.isEncrypt_communications(),
                        serverInfo.isEncrypt_messages(),
                        serverInfo.getAvailable_chats(),
                        serverInfo.isRequires_password(),
                        serverInfo.isStrictly_anonymous(),
                        serverInfo.getUsername_validation_regex(),
                        serverInfo.getUsername_validation_description()
                );
            } else {
                throw new InvalidPacketException("The received packet from the server was not recognised");
            }
        } catch (InvalidPacketException e) {
            throw new ClosedConnectionException("The server packet is invalid", e);
        } catch (ConnectionClosedException e) {
            throw new ClosedConnectionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ClosedConnectionException("An IOException occurred while trying to read data", e);
        }
        ServerConnection connection = new ServerConnection(client, connectionDetails, connectionWriter, connectionReader, serverInformation, socket);
        streamsTransfered = true;


       return connection;
    }

    private <T extends Packet> T readPacket() throws IOException, ClosedConnectionException, InvalidPacketException {



        return connectionReader.readPacket();
    }


    /**
     * Stop any currently ongoing connection process and eliminate this thread.
     */
    public void shutdown() throws IOException {
        synchronized (lock) {
            isRunning = false;

            if(!streamsTransfered) {
                socket.close();
                connectionWriter.close();
                connectionReader.close();
            } else {
                connectionWriter = null;
                connectionReader = null;
            }
            Thread.currentThread().interrupt();
        }
    }
}
