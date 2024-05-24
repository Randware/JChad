package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.server.model.server.ConnectionClosedException;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationResponsePacket;
import net.jchad.shared.networking.packets.encryption.AESencryptionKeysPacket;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.messages.MessageStatusFailedPacket;
import net.jchad.shared.networking.packets.messages.ServerMessagePacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import java.net.Socket;
import java.util.stream.Collectors;

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

    private final AESencryptionKeysPacket keys;

    private final ConnectionDetails connectionDetails;

    private ServerInformation serverInformation;

    private final Socket socket;

    private final CrypterManager crypterManager = new CrypterManager();

    /**
     * The {@link ConnectionWriter} which will be used to send data to the server.
     */
    private ConnectionWriter connectionWriter;

    /**
     * The {@link ConnectionReader} which will be used to receive data from the server.
     */
    private ConnectionReader connectionReader;

    public ServerConnection(Client client, ConnectionDetails connectionDetails, ConnectionWriter connectionWriter,
                            ConnectionReader connectionReader, ServerInformation serverInformation, Socket socket,AESencryptionKeysPacket keys ) {
        this.client = client;
        this.viewCallback = client.getViewCallback();
        this.connectionDetails = connectionDetails;
        this.connectionWriter = connectionWriter;
        this.connectionReader = connectionReader;
        this.serverInformation = serverInformation;
        this.socket = socket;
        this.keys = keys;
    }

    /**
     * This method executes the main code for the server connection.
     *
     * @throws ClosedConnectionException if the connection was closed for some reason.
     */
    @Override
    public Void call() throws ClosedConnectionException, IOException, InvalidPacketException {
        while(true) {
            Packet readPacket = readPacket();

            // first check if the connection was closed by the server
            if(readPacket instanceof ConnectionClosedPacket packet) {
                throw new ClosedConnectionException(packet.getMessage());
            }

            // check if the server sent new information about its configuration
            if(readPacket instanceof ServerInformationResponsePacket packet) {
                this.serverInformation = new ServerInformation(
                        packet.getServer_version(),
                        packet.isEncrypt_communications(),
                        packet.isEncrypt_messages(),
                        packet.getAvailable_chats(),
                        packet.isRequires_password(),
                        packet.isStrictly_anonymous(),
                        packet.getUsername_validation_regex(),
                        packet.getUsername_validation_description()
                );

                ArrayList<ClientChat> newChats = new ArrayList<>();

                for(String chat : serverInformation.available_chats()) {
                    newChats.add(new ClientChat(chat));
                }

                client.updateChats(newChats);
            }

            // check if there was a new message sent by someone, if yes add it to the client
            if(readPacket instanceof ServerMessagePacket packet) {
                if (keys != null && serverInformation.encrypt_messages()) {
                    crypterManager.setAESkey(keys.getMessage_key());
                    crypterManager.setAESkey(keys.getMessage_initialization_vector());
                    try {
                        client.addMessage(client.getChat(packet.getChat()),
                                new ClientChatMessage(
                                        packet.getChat(),
                                        crypterManager.encryptAES(packet.getMessage()),
                                        packet.getUsername(),
                                        packet.getTimestamp()
                                ));
                    } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                             NoSuchAlgorithmException | BadPaddingException | InvalidKeyException |
                             ImpossibleConversionException e) {
                     throw new ConnectionClosedException("An encryption related error occurred while trying to encrypt a message.", e);
                    }
                } else {
                    client.addMessage(client.getChat(packet.getChat()),
                            new ClientChatMessage(
                                    packet.getChat(),
                                    packet.getMessage(),
                                    packet.getUsername(),
                                    packet.getTimestamp()
                            ));
                }
            }

            // this packet will be sent by the server if the client sent an invalid message
            if(readPacket instanceof MessageStatusFailedPacket packet) {
                viewCallback.handleWarning("Server failed receiving message packet: " + packet.getReason());
            }
        }
    }

    /**
     * Stop any currently running processes and also close all streams.
     */
    public void closeConnection() {
        // Do everything that must be done in order to properly close the current connection
        try {
            connectionWriter.close();
            connectionReader.close();
            socket.close();
        } catch (IOException e) {
            client.getViewCallback().handleFatalError(new IOException("An error occurred while closing the connection to the server.", e));
        }
    }

    /**
     * This method converts the specified message content and message chat into a {@link ClientChatMessage}.
     * It will be sent to the server and then returned.
     *
     * @param messageContent the content of the message.
     * @param chat the {@link ClientChat} this message was sent in.
     * @return the converted and sent {@link ClientChatMessage}
     */
    public ClientChatMessage sendMessage(String messageContent, ClientChat chat) throws ClosedConnectionException {
        ClientChatMessage message = new ClientChatMessage(chat.getName(), messageContent, connectionDetails.getUsername(), System.currentTimeMillis());
        if (serverInformation.encrypt_messages() && keys != null) {
            try {

                crypterManager.setAESkey(keys.getMessage_key());
                crypterManager.setBase64IV(keys.getMessage_initialization_vector());
                ClientMessagePacket encryptedMessage =
                        new ClientMessagePacket(crypterManager.encryptAES(messageContent), chat.getName());
                writePacket(encryptedMessage);
            } catch (ImpossibleConversionException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                     IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                client.getViewCallback().handleFatalError(new Exception("Message could not be encrypted", e));
            }
        } else {
            writePacket(new ClientMessagePacket(messageContent, chat.getName()));
        }
        return message;
    }

    /**
     * Get the currently known information about the server.
     *
     * @return the currently known {@link ServerInformation}-
     */
    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    public  <T extends Packet> void writePacket(T packet) throws ClosedConnectionException {
        try {
            if (keys != null && serverInformation.encrypt_communications()) {
                crypterManager.setAESkey(keys.getCommunication_key());
                crypterManager.setBase64IV(keys.getCommunication_initialization_vector());

                connectionWriter.send(crypterManager.encryptAES(packet.toJSON()));

            } else {
                connectionWriter.sendPacket(packet);
            }
        } catch (ImpossibleConversionException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new ClosedConnectionException("An unknown encryption related error occurred while trying to send a Packet", e);
        }
    }

    public  <T extends Packet> T readPacket() throws IOException, ClosedConnectionException, InvalidPacketException {

        try {
            if (keys != null && serverInformation.encrypt_communications()) {
                crypterManager.setAESkey(keys.getCommunication_key());
                crypterManager.setBase64IV(keys.getCommunication_initialization_vector());

                return Packet.fromJSON(crypterManager.decryptAES(connectionReader.read()));

            } else {
                return connectionReader.readPacket();
            }
        } catch (ImpossibleConversionException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new ClosedConnectionException("An unknown encryption related error occurred while trying to send a Packet", e);
        }
    }
}
