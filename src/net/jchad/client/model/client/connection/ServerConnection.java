package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.client.packets.PacketHandler;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;

/**
 * This class represents an active connection to a server. It will do things such
 * as informing the front end of new display information, handle the Threads responsible
 * for reading from and writing to the server, manage the decryption and deserialization process
 * and execute the code responsible for a specific packet type.
 *
 * TODO: Implement proper closing functionality
 * TODO: Maybe handle chats in here?
 */
public final class ServerConnection extends Thread implements PacketHandler {
    private Client client;

    private final ViewCallback viewCallback;

    private final ConnectionDetails connectionDetails;

    /**
     * The {@link ConnectionWriter} which will be used to send data to the server.
     */
    private ConnectionWriter connectionWriter;

    /**
     * The {@link ConnectionReader} which will be used to receive data from the server.
     */
    private ConnectionReader connectionReader;

    public ServerConnection(Client client, ConnectionDetails connectionDetails, ConnectionWriter connectionWriter, ConnectionReader connectionReader) {
        this.client = client;
        this.viewCallback = client.getViewCallback();
        this.connectionDetails = connectionDetails;
        this.connectionWriter = connectionWriter;
        this.connectionReader = connectionReader;
    }

    /**
     * Stop any currently running processes and also close all streams.
     */
    public void closeConnection() {
        // Do everything that must be done in order to properly close the current connection
    }

    /**
     * This method converts the specified message content and message chat into a {@link ClientChatMessage}.
     * It will be sent to the server and then returned.
     *
     * @param messageContent the content of the message.
     * @param chat the {@link ClientChat} this message was sent in.
     * @return the converted and sent {@link ClientChatMessage}
     */
    public ClientChatMessage sendMessage(String messageContent, ClientChat chat) {
        ClientChatMessage message = new ClientChatMessage(chat.getName(), messageContent, connectionDetails.getUsername(), System.currentTimeMillis());

        connectionWriter.sendPacket(new ClientMessagePacket(messageContent, chat.getName()));

        return message;
    }

    @Override
    public void handlePacketString(String string) {
        /**
         * Check if encryption is enabled -> if yes decrypt string
         * Convert string into packet object
         * PacketMapper.executePacket(packet, this);
         */
    }

    @Override
    public void handlePacketReaderError(Exception e) {
        viewCallback.handleError(e);
    }
}
