package net.jchad.client.model.client;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.config.ClientConfigManager;
import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;

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
    private final ServerConnector serverConnector;
    private ServerConnection currentConnection;
    private ArrayList<ClientChat> chats;

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
     * This method converts a simple message string into a complete
     * {@link ClientChatMessage} and then adds it to the messages like
     * any other message. This is method is used to easily send messages from
     * the view.
     *
     * @param messageString the message string which
     */
    public void sendMessageString(String messageString) {
        ClientChatMessage message = new ClientChatMessage(messageString);
        message.setOwn(true);

        addMessage(currentChat, message);
    }

    /**
     * Adds the specified {@link ClientChatMessage} to the specified {@link ClientChat}.
     * If the message was sent in the currently active chat, it also notifies the view
     * that it needs to display this new message.
     *
     * @param chat the {@link ClientChat} in which the specified {@link ClientChatMessage} should be sent.
     * @param message the {@link ClientChatMessage} that should be sent in the specified {@link ClientChat}.
     */
    private void addMessage(ClientChat chat, ClientChatMessage message) {
        chat.addMessage(message);

        if(chat.equals(currentChat)) {
            displayMessage(message);
        }
    }

    /**
     * Depending on if the message is "own", meaning the message was sent by this client, this method calls the
     * displayOwnMessage() or the displayOtherMessage() method so the frontend can visually differentiate
     * both of these message types.
     *
     * @param message the {@link ClientChatMessage} that should be displayed.
     */
    private void displayMessage(ClientChatMessage message) {
        if(message.isOwn()) {
            viewCallback.displayOwnMessage(message);
        } else {
            viewCallback.displayOtherMessage(message);
        }
    }

    /**
     * This method sets the currentChat variable to the specified {@link ClientChat}.
     * It also calls the displayMessage() method for every {@link ClientChatMessage} in
     * the specified {@link ClientChat}.
     * <br>
     * <br>
     * <font color="red">It is important that this method gets called after the view has
     * updated its current chat display otherwise it can lead to duplicate chat messages being displayed.</font>
     *
     * @param chat the {@link ClientChat} that should be set as the current chat.
     */
    public void setCurrentChat(ClientChat chat) {
        this.currentChat = chat;

       for(ClientChatMessage message : chat.getMessages()) {
           displayMessage(message);
       }
    }

    /**
     * Returns the corresponding {@link ClientChat} for a chat name.
     * Return null if no chat was found with this name.
     *
     * @param name the chat name for which to retrieve the {@link ClientChat} instance.
     * @return the {@link ClientChat} instance for this chat name, null if no chat with this name was found.
     */
    public ClientChat getChat(String name) {
        for(ClientChat chat : chats) {
            if(chat.getName().equals(name)) {
                return chat;
            }
        }

        return null;
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
