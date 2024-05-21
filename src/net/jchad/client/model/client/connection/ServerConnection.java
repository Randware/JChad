package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.ViewCallback;

/**
 * This class represents an active connection to a server. It will do things such
 * as informing the front end of new display information, handle the Threads responsible
 * for reading from and writing to the server, manage the decryption and deserialization process
 * and execute the code responsible for a specific packet type.
 */
public class ServerConnection {
    private final ViewCallback viewCallback;

    /**
     * The {@link ConnectionWriter} which will be used to send data to the server.
     */
    private ConnectionWriter connectionWriter;

    /**
     * The {@link ConnectionReader} which will be used to receive data from the server.
     */
    private ConnectionReader connectionReader;

    public ServerConnection(ViewCallback viewCallback, ConnectionWriter connectionWriter, ConnectionReader connectionReader) {
        this.viewCallback = viewCallback;
        this.connectionWriter = connectionWriter;
        this.connectionReader = connectionReader;
    }

    /**
     * Stop any currently running processes and also close all Streams.
     */
    public void closeConnection() {
        // Do everything that must be done in order to properly close the current connection
    }


}
