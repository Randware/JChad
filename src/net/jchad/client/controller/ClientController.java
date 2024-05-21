package net.jchad.client.controller;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.client.config.ClientConfigManager;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;

import java.util.ArrayList;

/**
 * Used for controlling the {@link Client} from the view.
 *
 * TODO: Figure out what should happen when a method is called that requires a connection, when
 *          no connection is present.
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
     * Disconnect from both the server connecting process and the current connection.
     */
    public void disconnect() {
        client.disconnect();
    }

    /**
     * This method returns the {@link ClientConfigManager} currently used by the client.
     * The {@link ClientConfigManager} has all sorts of methods to modify the configuration.
     * Use this method if you want to manage saved connections, modify configuration values,
     * or just do anything that modifies the client configuration in general.
     *
     * @return the {@link ClientConfigManager} currently used by the client.
     */
    public ClientConfigManager configuration() {
        return client.getConfigManager();
    }

    /**
     * Send the specified string as a message into the current channel
     *
     * @param message the message string that should be sent.
     */
    public void sendMessage(String message) {
        client.sendMessageString(message);
    }

    /**
     * This method should be called when the user opens a new chat. It will send all messages for
     * the specified {@link ClientChat} to the view, so it can display them.
     *
     * @param chat the {@link ClientChat} that was opened by the user.
     */
    public void setCurrentChat(ClientChat chat) {
        client.setCurrentChat(chat);
    }

    /**
     * Returns all {@link ClientChatMessage} instances from this specific chat.
     * <br>
     * <br>
     * <font color="red">This method is not implemented yet, it currently returns an empty list.</font>
     *
     * @param chat the chat from which the {@link ClientChatMessage} instances
     *             should be returned.
     * @return an {@link ArrayList} containing all {@link ClientChatMessage} instances
     * from this chat.
     */
    public ArrayList<ClientChatMessage> getChatMessages(ClientChat chat) {
        return new ArrayList<>();
    }
}
