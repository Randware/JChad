package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.connection.ConnectionDetails;

/**
 * This class is responsible for everything that needs to be done before the client
 * can successfully connect to the server. Examples of that may be connecting to the server,
 * exchanging encryption information and handling the password verification process.
 */
public class ServerConnector {
    private ViewCallback viewCallback;

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

    public ServerConnector(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
    }

    /**
     * Establish a connection to a server using the given {@link ConnectionDetails}.
     * Throws exception if something goes wrong during the connecting process and
     * a connection can't be established. Returns a valid {@link ServerConnection} if
     * a connection was successfully established.
     *
     * @param connectionDetails the {@link ConnectionDetails} which will be used to establish a connection.
     * @return a valid {@link ServerConnection} if a connection was successfully established.
     */
    public ServerConnection connect(ConnectionDetails connectionDetails) {
        // Do everything that must be done in order to establish a connection

        return new ServerConnection(viewCallback, connectionWriter, connectionReader);
    }

    /**
     * Stop any currently ongoing connection process.
     */
    public void stop() {

    }
}
