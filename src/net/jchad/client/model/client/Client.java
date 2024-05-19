package net.jchad.client.model.client;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.config.ClientConfigManager;
import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.connection.ConnectionDetails;

/**
 * This class represents the entire backend infrastructure for the client.
 * It holds information such as the {@link ServerConnection}. It is also
 * the class that will be held by the {@link ClientController} and all the
 * methods from the view will be called on.
 */
public class Client {
    private ViewCallback viewCallback;
    private final ClientConfigManager configManager;
    private final ServerConnector serverConnector;
    private ServerConnection currentConnection;

    /**
     * This variable stores the chat the client is currently in.
     * This is needed, because the view needs to be informed when it
     * is supposed to display a new message. Still need to figure out how
     * to effectively update this when the client goes into another chat.
     * Maybe create a method "setCurrentChat(ClientChat chat)" which
     * returns all messages from the specified chat and updates this
     * variable.
     */
    private ClientChat currentChat;

    public Client(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
        this.configManager = new ClientConfigManager(viewCallback);
        this.serverConnector = new ServerConnector(viewCallback);
    }

    /**
     * This method will try to start a {@link ServerConnection} connection with
     * the provided {@link ConnectionDetails}.
     *
     * @param connectionDetails the {@link ConnectionDetails} that should be
     *                          used to establish a {@link ServerConnection}.
     */
    public void connect(ConnectionDetails connectionDetails) {
        this.currentConnection = serverConnector.connect(connectionDetails);
    }

    /**
     * Properly close the current {@link ServerConnection}.
     */
    public void disconnect() {
        currentConnection.closeConnection();
    }

    /**
     * Stop any currently ongoing server connection process.
     */
    public void stopConnecting() {
        serverConnector.stop();
    }

    /**
     * This method returns the {@link ClientConfigManager} currently used by the client.
     *
     * @return the {@link ClientConfigManager} currently used by the client.
     */
    public ClientConfigManager getConfigManager() {
        return configManager;
    }
}
