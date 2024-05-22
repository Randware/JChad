package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.client.packets.PacketHandler;
import net.jchad.client.model.client.packets.PacketMapper;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * This class is responsible for everything that needs to be done before the client
 * can successfully connect to the server. Examples of that may be connecting to the server,
 * exchanging encryption information and handling the password verification process.
 */
public final class ServerConnector extends Thread implements PacketHandler {
    private static final Object lock = new Object();

    private volatile boolean isRunning;
    private boolean streamsTransfered;

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

    public ServerConnector(Client client) {
        this.client = client;
        this.viewCallback = client.getViewCallback();
        this.isRunning = false;
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
    public ServerConnection connect(ConnectionDetails connectionDetails) throws Exception {
        synchronized (lock) {
            if (isRunning) {
                shutdown();
            }
            isRunning = true;
            start();
        }

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
            connectionReader = new ConnectionReader(socket.getInputStream(), this);
        } catch (IOException e) {
            throw new ClosedConnectionException("Could not open output and input streams for connection", e);
        }

        ServerConnection connection = new ServerConnection(client, connectionDetails, connectionWriter, connectionReader);
        connection.start();
        streamsTransfered = true;

        return connection;
    }

    /**
     * Stop any currently ongoing connection process and eliminate this thread.
     */
    public void shutdown() throws Exception {
        synchronized (lock) {
            isRunning = false;
            interrupt();

            if(!streamsTransfered) {
                connectionWriter.close();
                connectionReader.close();
            } else {
                connectionWriter = null;
                connectionReader = null;
            }
        }
    }

    @Override
    public void handlePacketString(String string) {
        /**
         * Check if encryption is enabled -> if yes decrypt string
         * Convert string into packet object
         * PacketMapper.executePacket(packet, this);
         */
        viewCallback.handleInfo(string);
    }

    @Override
    public void handlePacketReaderError(Exception e) {
        viewCallback.handleError(e);
    }
}
