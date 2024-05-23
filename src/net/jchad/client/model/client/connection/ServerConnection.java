package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.messages.ServerMessagePacket;

import java.io.IOException;
import java.util.concurrent.Callable;

import java.net.Socket;

/**
 * This class represents an active connection to a server. It will do things such
 * as informing the front end of new display information, handle the Threads responsible
 * for reading from and writing to the server, manage the decryption and deserialization process
 * and execute the code responsible for a specific packet type.
 *
 * TODO: Implement proper closing functionality
 * TODO: Maybe handle chats in here?
 */
public class ServerConnection implements Callable<Void> {
    private Client client;

    private final ViewCallback viewCallback;

    private final ConnectionDetails connectionDetails;

    private final ServerInformation serverInformation;

    private final Socket socket;

    /**
     * The {@link ConnectionWriter} which will be used to send data to the server.
     */
    private ConnectionWriter connectionWriter;

    /**
     * The {@link ConnectionReader} which will be used to receive data from the server.
     */
    private ConnectionReader connectionReader;

    public ServerConnection(Client client, ConnectionDetails connectionDetails, ConnectionWriter connectionWriter, ConnectionReader connectionReader, ServerInformation serverInformation, Socket socket) {
        this.client = client;
        this.viewCallback = client.getViewCallback();
        this.connectionDetails = connectionDetails;
        this.connectionWriter = connectionWriter;
        this.connectionReader = connectionReader;
        this.serverInformation = serverInformation;
        this.socket = socket;
    }

    /**
     * This method executes the main code for the server connection.
     *
     * @throws ClosedConnectionException if the connection was closed for some reason.
     */
    @Override
    public Void call() throws ClosedConnectionException, IOException, InvalidPacketException {
        while(true) {
            Packet packet = connectionReader.readPacket();

            // check for possible packet types

            if (packet instanceof ServerMessagePacket) {
                // handle message packet here
            }
        }
    }

    /**
     * Stop any currently running processes and also close all streams.
     */
    public void closeConnection() throws IOException {
        // Do everything that must be done in order to properly close the current connection
        connectionWriter.close();
        connectionReader.close();
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
}
