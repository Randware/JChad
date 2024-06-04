package net.jchad.client.model.client;

import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.config.ClientConfigManager;
import net.jchad.client.model.client.connection.ClosedConnectionException;
import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;
import net.jchad.client.model.client.connection.ServerInformation;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class represents the entire backend infrastructure for the client.
 * It holds information such as the {@link ServerConnection}. It is also
 * the class that will be held by the {@link ClientController} and all the
 * methods from the view will be called on.
 */
public class Client {
    private final ViewCallback viewCallback;
    private final ClientConfigManager configManager;
    private ServerConnector serverConnector = null;
    private final ExecutorService executorService;
    private ServerConnection currentConnection;
    private ArrayList<ClientChat> chats;

    /**
     * This variable stores the chat the client is currently in.
     * This is needed, because the view needs to be informed when it
     * is supposed to display a new message.
     */
    private ClientChat currentChat;

    public Client(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
        executorService = Executors.newFixedThreadPool(2);
        this.configManager = new ClientConfigManager(viewCallback);
        this.chats = new ArrayList<>();
    }

    /**
     * This method will try to start a {@link ServerConnection} connection with
     * the provided {@link ConnectionDetails}.
     *
     * @param connectionDetails the {@link ConnectionDetails} that should be
     *                          used to establish a {@link ServerConnection}.
     */
    public void connect(ConnectionDetails connectionDetails) {

        serverConnector = new ServerConnector(this, connectionDetails);
        try {
            if(currentConnection != null ) {
                currentConnection.closeConnection();
            }

            Future<ServerConnection> future = executorService.submit(serverConnector);
            currentConnection = future.get();

            serverConnector.shutdown();

            new Thread(currentConnection).start();
        } catch (Exception e) {
            viewCallback.handleFatalError(e);
        }
    }

    /**
     * Properly close the current {@link ServerConnection} and the {@link ServerConnector}
     * if there is an ongoing connection process.
     */
    public void disconnect() {
            if(currentConnection != null) {
                currentConnection.closeConnection();
            }

            if(serverConnector != null) {
                serverConnector.shutdown();
            }

        currentConnection = null;
    }

    /**
     * This method hands the specified message string to the currentConnection,
     * so it will be sent to the server. It also adds the chat message to the chat.
     *
     * @param messageString the message string which should be sent.
     */
    public void sendMessageString(String messageString) {
        if(currentConnection != null && currentChat != null) {
            try {
                ClientChatMessage message = currentConnection.sendMessage(messageString, currentChat);
                message.setOwn(true);
                addMessage(currentChat, message);
            } catch (ClosedConnectionException e) {
                viewCallback.handleFatalError(e);
                currentConnection.closeConnection();
            }
        }
    }

    /**
     * Adds the specified {@link ClientChatMessage} to the specified {@link ClientChat}.
     * If the message was sent in the currently active chat, it also notifies the view
     * that it needs to display this new message.
     *
     * @param chat    the {@link ClientChat} in which the specified {@link ClientChatMessage} should be sent.
     * @param message the {@link ClientChatMessage} that should be sent in the specified {@link ClientChat}.
     */
    public void addMessage(ClientChat chat, ClientChatMessage message) {
        chat.addMessage(message);

        if (chat.equals(currentChat)) {
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
        if (message.isOwn()) {
            viewCallback.displayOwnMessage(message);
        } else {
            viewCallback.displayOtherMessage(message);
        }
    }

    /**
     * This method sets the currentChat variable to the specified {@link ClientChat}.
     * It also calls the displayMessage() method for every {@link ClientChatMessage} in
     * the specified {@link ClientChat}. It also has some safety mechanisms in order to
     * avoid adding messages to the wrong chat, if the user switches chat before messages finished loading.
     * <br>
     * <br>
     * <font color="red">It is important that this method gets called after the view has
     * updated its current chat display otherwise it can lead to messages being displayed in
     * the wrong chat.</font>
     *
     * @param chat the {@link ClientChat} that should be set as the current chat.
     */
    public void setCurrentChat(ClientChat chat) {
        if(!chat.isJoined()) {

            try {
                currentConnection.joinChat(chat.getName());
            } catch (ClosedConnectionException e) {
                currentConnection.closeConnection();
                return;
            }

            chat.setJoined(true);
        }

        this.currentChat = chat;

        ArrayList<ClientChatMessage> messages = chat.getMessages();

        for (int x = 0; this.currentChat.equals(chat) && x < messages.size(); x++) {
            displayMessage(messages.get(x));
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
        for (ClientChat chat : chats) {
            if (chat.getName().equals(name)) {
                return chat;
            }
        }

        return null;
    }

    /**
     * Get all currently available chats from the client.
     *
     * @return an {@link ArrayList} of all currently available
     * {@link ClientChat} instances.
     */
    public ArrayList<ClientChat> getChats() {
        return new ArrayList<>(chats);
    }

    /**
     * This will update the current chat configuration with the provided one.
     *
     * @param updatedChats the updated chat configuration.
     */
    public void updateChats(ArrayList<ClientChat> updatedChats) {
        this.chats = updatedChats;

        viewCallback.updateDisplay();
    }

    /**
     * This method returns the {@link ClientConfigManager} currently used by the client.
     *
     * @return the {@link ClientConfigManager} currently used by the client.
     */
    public ClientConfigManager getConfigManager() {
        return configManager;
    }

    public ViewCallback getViewCallback() {
        return viewCallback;
    }

    /**
     * <b>Returns a <u>COPY</u> of the current server information or null if the server infos are not known yet</b>
     * @return a copy of the server infos or null if the connection was not established yet
     */
    public ServerInformation getServerInformation() {
        if (currentConnection != null && currentConnection.getServerInformation() != null) {
            final ServerInformation temp = currentConnection.getServerInformation();
            return new ServerInformation(
                    temp.server_version(),
                    temp.encrypt_communications(),
                    temp.encrypt_messages(),
                    temp.available_chats(),
                    temp.requires_password(),
                    temp.strictly_anonymous(),
                    temp.username_validation_regex(),
                    temp.username_validation_description()
            );
        } else {
            return null;
        }
    }
}
