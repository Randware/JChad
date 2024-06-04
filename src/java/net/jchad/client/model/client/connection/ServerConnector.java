package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.defaults.*;
import net.jchad.shared.networking.packets.encryption.AESencryptionKeysPacket;
import net.jchad.shared.networking.packets.encryption.KeyExchangeStartPacket;
import net.jchad.shared.networking.packets.encryption.RSAkeyErrorPacket;
import net.jchad.shared.networking.packets.encryption.RSAkeyPacket;
import net.jchad.shared.networking.packets.password.PasswordFailedPacket;
import net.jchad.shared.networking.packets.password.PasswordRequestPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.password.PasswordSuccessPacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;
import net.jchad.shared.networking.packets.username.UsernameServerPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.Callable;


/**
 * This class is responsible for everything that needs to be done before the client
 * can successfully connect to the server. Examples of that may be connecting to the server,
 * exchanging encryption information and handling the password verification process.
 */
public class ServerConnector implements Callable<ServerConnection> {
    private static final Object lock = new Object();

    private volatile boolean isRunning;
    private boolean streamsTransfered;
    private final ConnectionDetails connectionDetails;
    private Client client;
    private ViewCallback viewCallback;
    private ServerInformation serverInformation;
    private final CrypterManager crypterManager = new CrypterManager();
    private AESencryptionKeysPacket keys = null;



    /**
     * The Socket the connection will run on.
     */
    private Socket socket;

    /**
     * The {@link ConnectionWriter} which will be used to send data to the server.
     * This threads ownership will be moved to the {@link ServerConnection} once the connection
     * was successfully established.
     */
    private ConnectionWriter connectionWriter;

    /**
     * The {@link ConnectionReader} which will be used to receive data from the server.
     * This threads ownership will be moved to the {@link ServerConnection} once the connection
     * was successfully established.
     */
    private ConnectionReader connectionReader;

    public ServerConnector(Client client, ConnectionDetails connectionDetails) {
        if (client == null || connectionDetails == null) {
            throw new NullPointerException("The parameters can not be null");
        }
        this.client = client;
        this.viewCallback = client.getViewCallback();
        this.isRunning = false;
        this.connectionDetails = connectionDetails;
        streamsTransfered = false;

    }

    /**
     * Establish a connection to a server using the given {@link ConnectionDetails}.
     * Throws exception if something goes wrong during the connecting process and
     * a connection can't be established. Returns a valid {@link ServerConnection} if
     * a connection was successfully established.
     *
     * @param connectionDetails the {@link ConnectionDetails} which will be used to establish a connection.
     * @return a valid {@link ServerConnection} if a connection was successfully established.
     * @throws ClosedConnectionException if an error occurred during the connection process.
     */
    @Deprecated(forRemoval = true)
    public ServerConnection connect(ConnectionDetails connectionDetails) throws Exception {
        synchronized (lock) {
            if (isRunning) {
                shutdown();
            }
            isRunning = true;

        }
            return null;
    }

    @Override
    public ServerConnection call() throws IOException, ClosedConnectionException {
        if (isRunning) {
            shutdown();
        }

        isRunning = true;
        String host = connectionDetails.getHost();
        int port = connectionDetails.getPort();

        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            throw new ClosedConnectionException("Could not connect to host \"%s\" on port \"%s\"".formatted(host, port), e);
        }

        try {
            connectionWriter = new ConnectionWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new ClosedConnectionException("Could not open output and input streams for connection", e);
        }

        try {
            connectionReader = new ConnectionReader(socket.getInputStream());
            connectionReader.start();

        } catch (IOException e) {
            throw new ClosedConnectionException("Could not open output and input streams for connection", e);
        }

        try {
            Packet incomingPacket = readPacket();
            if (incomingPacket.getClass().equals(NotWhitelistedPacket.class)) {
                throw new ClosedConnectionException("Client is not whitelisted on this server");
            }
            if (incomingPacket.getClass().equals(BannedPacket.class)) {
                throw new ClosedConnectionException("Client is banned on this server");
            }
            if (incomingPacket.getClass().equals(ServerInformationResponsePacket.class)) {
                ServerInformationResponsePacket serverInfo = (ServerInformationResponsePacket) incomingPacket;
                serverInformation = new ServerInformation(
                        serverInfo.getServer_version(),
                        serverInfo.isEncrypt_communications(),
                        serverInfo.isEncrypt_messages(),
                        serverInfo.getAvailable_chats(),
                        serverInfo.isRequires_password(),
                        serverInfo.isStrictly_anonymous(),
                        serverInfo.getUsername_validation_regex(),
                        serverInfo.getUsername_validation_description()
                );
            } else {
                throw new InvalidPacketException("The received packet from the server was not recognised");
            }
            Packet nextPacket1 = connectionReader.readPacket();
            boolean success = encryption(nextPacket1);
            Packet nextPacket2 = password((success) ? null : nextPacket1);
            username(nextPacket2);

            Packet hopefullySuccess = readPacket();
            Packet newServerInfos = readPacket();
            if (hopefullySuccess!= null  && hopefullySuccess.getClass().equals(ConnectionEstablishedPacket.class) &&
                    newServerInfos != null && newServerInfos.getClass().equals(ServerInformationResponsePacket.class)) {

                    ServerInformationResponsePacket newServerInfosCasted = (ServerInformationResponsePacket) newServerInfos;
                    serverInformation = new ServerInformation(
                            newServerInfosCasted.getServer_version(),
                            newServerInfosCasted.isEncrypt_communications(),
                            newServerInfosCasted.isEncrypt_messages(),
                            newServerInfosCasted.getAvailable_chats(),
                            newServerInfosCasted.isRequires_password(),
                            newServerInfosCasted.isStrictly_anonymous(),
                            newServerInfosCasted.getUsername_validation_regex(),
                            newServerInfosCasted.getUsername_validation_description()
                    );

                    ArrayList<ClientChat> newChats = new ArrayList<>();
                    for(String chat : serverInformation.available_chats()) {
                        newChats.add(new ClientChat(chat));
                    }
                    client.updateChats(newChats);

                    ServerConnection connection = new ServerConnection(client, connectionDetails, connectionWriter, connectionReader, serverInformation, socket, keys);
                    streamsTransfered = true;
                    return connection;




            } else {
                throw new ClosedConnectionException("The connection could NOT be established");
            }


        } catch (InvalidPacketException e) {
            throw new ClosedConnectionException("The server packet is invalid", e);
        } catch (ClosedConnectionException e) {
            throw new ClosedConnectionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ClosedConnectionException("An IOException occurred while trying to read data", e);
        }




    }

    private <T extends Packet> void username(T packet) throws ClosedConnectionException, IOException, InvalidPacketException {
        for (int i = 0; true; i++) {
            if (packet == null || i > 0) {
                packet = readPacket();

            }
            if (packet == null) throw new ClosedConnectionException("The connection to the server was lost during the username process");
            if (packet.getClass().equals(ConnectionClosedPacket.class)) {
                throw new ClosedConnectionException(((ConnectionClosedPacket) packet).getMessage());
            }
            if (packet.getClass().equals(UsernameServerPacket.class)) {
                UsernameServerPacket usernameServerPacket = (UsernameServerPacket) packet;
                if (usernameServerPacket.getUsername_response_type().equals(UsernameServerPacket.UsernameResponseType.PROVIDE_USERNAME)) {
                    writePacket(new UsernameClientPacket(connectionDetails.getUsername()));
                } else {
                    UsernameServerPacket.UsernameResponseType responseType = usernameServerPacket.getUsername_response_type();
                    switch (responseType) {
                        case ERROR_USERNAME_TAKEN -> {
                            connectionDetails.setUsername(client.getViewCallback().displayPrompt("Enter a username", "The requested username is already taken. Pleases enter a new one: "));
                            writePacket(new UsernameClientPacket(connectionDetails.getUsername()));
                        }
                        case ERROR_USERNAME_INVALID -> {
                            connectionDetails.setUsername(client.getViewCallback().displayPrompt("Enter a username", "The requested username is invalid make sure to follow these rules: " + serverInformation.username_validation_description()));
                            writePacket(new UsernameClientPacket(connectionDetails.getUsername()));
                        }
                        case ERROR_USERNAME_BLOCKED -> {
                            connectionDetails.setUsername(client.getViewCallback().displayPrompt("Enter a username", "The requested username is blacklisted. Please enter a new one: "));
                            writePacket(new UsernameClientPacket(connectionDetails.getUsername()));
                        }
                        default -> {
                            return;
                        }
                    }
                }

            }
        }
    }

    private <T extends Packet> Packet password(T packet) throws ClosedConnectionException, IOException, InvalidPacketException {
        while (true) {
            if (packet == null) {
                packet = readPacket();
            }
            if (packet == null) throw new ClosedConnectionException("The connection to the server was lost during the password authentication process");
            if (packet.getClass().equals(ConnectionClosedPacket.class)) {
                throw new ClosedConnectionException(((ConnectionClosedPacket) packet).getMessage());
            }
            if (packet.getClass().equals(PasswordRequestPacket.class)) {
                writePacket(new PasswordResponsePacket(CrypterManager.hash(connectionDetails.getPassword())));
                Packet nextNextPacket = readPacket(); //I just want to get over this project. I don't have enough energy to think of creative variable names.
                if (nextNextPacket == null) throw new ClosedConnectionException("The connection to the server was lost during the password authentication process");
                if (nextNextPacket.getClass().equals(PasswordSuccessPacket.class)) {
                    return null;
                }
                if (nextNextPacket.getClass().equals(PasswordFailedPacket.class)) {
                    connectionDetails.setPassword(client.getViewCallback().displayPrompt("Password", "The provided password was wrong. Please enter the correct one: "));
                }

            } else {
                return packet;
            }
        }

    }

    /**
     * Encrypts packets if the provided packet is a {@link KeyExchangeStartPacket}.
     * @param packet the next packet that was received by the server
     * @return if the keys got exchanged
     */
    private boolean encryption(Packet packet) throws ClosedConnectionException {
        if (packet.getClass().equals(KeyExchangeStartPacket.class)) {

            crypterManager.setKeyPair(4096);

             connectionWriter.sendPacket(new RSAkeyPacket(crypterManager.getPublicKey()));
            try {
                String keys = connectionReader.read();

                if (Packet.fromJSON(keys) == null) {
                    Packet keysPacket = Packet.fromJSON(crypterManager.decryptRSA(keys));
                    if ( keysPacket != null && keysPacket.getClass().equals(AESencryptionKeysPacket.class)) {
                        this.keys = (AESencryptionKeysPacket) keysPacket;
                        connectionReader.read(); //consumes the {"packet_type":"KEY_EXCHANGE_END"} Packet
                        return true;
                    } else {
                        throw new InvalidPacketException((keysPacket != null && keysPacket.getClass().equals(RSAkeyErrorPacket.class)) ? ((RSAkeyErrorPacket) keysPacket).getError_message() : "");
                    }
                } else {
                    throw new ClosedConnectionException("An error occurred during the key exchange process");
                }
            } catch (IOException e) {
                throw new ClosedConnectionException("An IOException occurred during the key exchange process", e);
            } catch (ClosedConnectionException e) {
                throw new ClosedConnectionException("The server closed the connection during the key exchange process", e);
            } catch (ImpossibleConversionException e) {
                throw new ClosedConnectionException("The server side encrypted encryption keys are not correctly encrypted during the key exchange process", e);
            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                     BadPaddingException e) {
                throw new ClosedConnectionException("An unknown error during the key exchange process", e);
            } catch (InvalidKeyException e) {
                throw new ClosedConnectionException("The server encrypted the keys incorrect during the key exchange process", e);
            } catch (InvalidPacketException e) {
                throw new ClosedConnectionException("The server sent wrong or invalid packets during the key exchange process", e);
            }
        } else {
            return false;
        }

    }

    private <T extends Packet> T readPacket() throws IOException, ClosedConnectionException, InvalidPacketException {



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

    private <T extends Packet> void writePacket(T packet) throws ClosedConnectionException {
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
            throw new ClosedConnectionException("An unknown encryption error occurred while trying to send a Packet", e);
        }
    }


    /**
     * Stop any currently ongoing connection process and eliminate this thread.
     */
    public void shutdown() {
        synchronized (lock) {
            isRunning = false;

            if(!streamsTransfered) {
                try {
                if (socket != null) {
                    socket.close();
                    connectionWriter.close();
                    connectionReader.close();
                }
            } catch (IOException e) {
                client.getViewCallback().handleFatalError(new IOException("An error occurred while closing the connector to the server.", e));
            }
            } else {
                connectionWriter = null;
                connectionReader = null;
            }
        }
    }
}
