package net.jchad.client.controller;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.connection.ConnectionDetails;

/**
 * Used for controlling the {@link Client} from the view.
 */
public class ClientController {
    private final Client client;

    public ClientController(ViewCallback viewCallback) {
        client = new Client(viewCallback);
    }

    public void connect(ConnectionDetails connectionDetails) {
        client.connect(connectionDetails);
    }
}
