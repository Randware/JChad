package net.jchad.client.model.client;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.store.connection.ConnectionDetails;

/**
 * This class represents the entire backend infrastructure for the client.
 * It holds information such as the {@link ServerConnection}. It is also
 * the class that will be held by the {@link ClientController} and all the
 * methods from the view will be called on.
 */
public class Client {
    private ViewCallback viewCallback;

    public Client(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
    }

    /**
     * This method will try to start a {@link ServerConnection} connection with
     * the provided {@link ConnectionDetails}.
     *
     * @param connectionDetails the {@link ConnectionDetails} that should be
     *                          used to establish a {@link ServerConnection}.
     */
    public void connect(ConnectionDetails connectionDetails) {

    }
}
