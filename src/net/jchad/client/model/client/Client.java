package net.jchad.client.model.client;

import com.google.gson.JsonParseException;
import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.config.ClientConfigManager;
import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.store.connection.ConnectionDetails;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This class represents the entire backend infrastructure for the client.
 * It holds information such as the {@link ServerConnection}. It is also
 * the class that will be held by the {@link ClientController} and all the
 * methods from the view will be called on.
 */
public class Client {
    private ViewCallback viewCallback;
    private final ClientConfigManager configManager;

    public Client(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
        this.configManager = new ClientConfigManager(viewCallback);

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

    /**
     * This method will save a new {@link ConnectionDetails} instance.
     *
     * @param connection the connection that should be saved.
     */
    public void addConnection(ConnectionDetails connection) {
        configManager.addConnection(connection);
    }

    public void removeConnection(ConnectionDetails connection) {
        configManager.removeConnection(connection);
    }

    /**
     * This method will return all currently stored {@link ConnectionDetails}.
     *
     * @return an {@link ArrayList} of all currently stored {@link ConnectionDetails}.
     */
    public ArrayList<ConnectionDetails> getConnections() {
        return configManager.getConnections();
    }
}
