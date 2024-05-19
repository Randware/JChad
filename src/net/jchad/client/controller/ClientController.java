package net.jchad.client.controller;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.connection.ConnectionDetails;

import java.util.ArrayList;

/**
 * Used for controlling the {@link Client} from the view.
 */
public class ClientController {
    private final Client client;

    public ClientController(ViewCallback viewCallback) {
        client = new Client(viewCallback);
    }

    /**
     * This methods tries to establish a connection to a server using the specified
     * {@link ConnectionDetails}.
     *
     * @param connectionDetails the {@link ConnectionDetails} that should be used for connection
     *                          establishment.
     */
    public void connect(ConnectionDetails connectionDetails) {
        client.connect(connectionDetails);
    }

    /**
     * Saves a new {@link ConnectionDetails} instance.
     *
     * @param connectionDetails the {@link ConnectionDetails} instance that should be saved.
     */
    public void addConnection(ConnectionDetails connectionDetails) {
        client.addConnection(connectionDetails);
    }

    /**
     * Removes the given {@link ConnectionDetails} instance from the saved connections,
     * if present.
     *
     * @param connectionDetails the {@link ConnectionDetails} instance that should be removed.
     */
    public void removeConnection(ConnectionDetails connectionDetails) {
        client.removeConnection(connectionDetails);
    }

    /**
     * This will return all saved {@link ConnectionDetails} instances.
     *
     * @return an {@link ArrayList} containing all saved {@link ConnectionDetails} instances.
     */
    public ArrayList<ConnectionDetails> getConnections() {
        return client.getConnections();
    }
}
