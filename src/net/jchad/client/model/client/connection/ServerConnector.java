package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.Client;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.client.packets.PacketHandler;
import net.jchad.client.model.client.packets.PacketMapper;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.server.model.server.ConnectionClosedException;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;
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
import java.util.concurrent.Callable;
import java.util.function.Consumer;

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
    private AESencryptionKeysPacket keys = new AESencryptionKeysPacket("", "", "" ,"");



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
        crypterManager.setKeyPair(4096);
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
            Packet nextPacket = connectionReader.readPacket();
            encryption(nextPacket);
            password();
            username();

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
                    ServerConnection connection = new ServerConnection(client, connectionDetails, connectionWriter, connectionReader, serverInformation, socket);
                    streamsTransfered = true;
                    return connection;




            } else {
                throw new ConnectionClosedException("The connection could NOT be established");
            }


        } catch (InvalidPacketException e) {
            throw new ClosedConnectionException("The server packet is invalid", e);
        } catch (ConnectionClosedException e) {
            throw new ClosedConnectionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ClosedConnectionException("An IOException occurred while trying to read data", e);
        }




    }

    private void username() throws ClosedConnectionException, IOException, InvalidPacketException {
        while (true) {
            Packet nextPacket = readPacket();
            if (nextPacket == null) throw new ConnectionClosedException("The connection to the server was lost during the username process");
            if (nextPacket.getClass().equals(ConnectionClosedPacket.class)) {
                throw new ClosedConnectionException(((ConnectionClosedPacket) nextPacket).getMessage());
            }
            if (nextPacket.getClass().equals(UsernameServerPacket.class)) {
                UsernameServerPacket usernameServerPacket = (UsernameServerPacket) nextPacket;
                if (usernameServerPacket.getUsername_response_type().equals(UsernameServerPacket.UsernameResponseType.PROVIDE_USERNAME)) {
                    writePacket(new UsernameClientPacket(connectionDetails.getUsername()));
                } else {
                    UsernameServerPacket.UsernameResponseType responseType = usernameServerPacket.getUsername_response_type();
                    switch (responseType) {
                        case ERROR_USERNAME_TAKEN -> connectionDetails.setUsername(client.getViewCallback().displayPrompt("Enter a username", "The requested username is already taken. Pleases enter a new one: "));
                        case ERROR_USERNAME_INVALID -> connectionDetails.setUsername(client.getViewCallback().displayPrompt("Enter a username", "The requested username is invalid make sure to follow these rules: " + serverInformation.username_validation_description()));
                        case ERROR_USERNAME_INAPROPRIATE -> connectionDetails.setUsername(client.getViewCallback().displayPrompt("Enter a username", "The requested username is inappropriate. Please enter a new one: "));
                        default -> {
                            return;
                        }
                    }
                }

            }
        }
    }

    private void password() throws ClosedConnectionException, IOException, InvalidPacketException {
        while (true) {
            Packet nextPacket = readPacket();
            if (nextPacket == null) throw new ConnectionClosedException("The connection to the server was lost during the password authentication process");
            if (nextPacket.getClass().equals(ConnectionClosedPacket.class)) {
                throw new ClosedConnectionException(((ConnectionClosedPacket) nextPacket).getMessage());
            }
            if (nextPacket.getClass().equals(PasswordRequestPacket.class)) {
                writePacket(new PasswordResponsePacket(CrypterManager.hash(connectionDetails.getPassword())));
                Packet nextNextPacket = readPacket(); //I just want to get over this project. I don't have enough energy to think of creative variable names.
                if (nextNextPacket == null) throw new ConnectionClosedException("The connection to the server was lost during the password authentication process");
                if (nextNextPacket.getClass().equals(PasswordSuccessPacket.class)) {
                    return;
                }
                if (nextNextPacket.getClass().equals(PasswordFailedPacket.class)) {
                    client.getViewCallback().displayPrompt("Password", "The provided password was wrong. Please enter the correct one: ");
                }
                throw new ConnectionClosedException("An unknown error occurred during the password authentication process");
            }
        }

    }

    /**
     * Encrypts packets if the provided packet is a {@link KeyExchangeStartPacket}.
     * @param packet the next packet that was received by the server
     * @return if the keys got exchanged
     */
    private boolean encryption(Packet packet) {
        if (packet.getClass().equals(KeyExchangeStartPacket.class)) {
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
                    throw new ConnectionClosedException("An error occurred during the key exchange process");
                }
            } catch (IOException e) {
                throw new ConnectionClosedException("An IOException occurred during the key exchange process", e);
            } catch (ClosedConnectionException e) {
                throw new ConnectionClosedException("The server closed the connection during the key exchange process", e);
            } catch (ImpossibleConversionException e) {
                throw new ConnectionClosedException("The server side encrypted encryption keys are not correctly encrypted during the key exchange process", e);
            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                     BadPaddingException e) {
                throw new ConnectionClosedException("An unknown error during the key exchange process", e);
            } catch (InvalidKeyException e) {
                throw new ConnectionClosedException("The server encrypted the keys incorrect during the key exchange process", e);
            } catch (InvalidPacketException e) {
                throw new ConnectionClosedException("The server sent wrong or invalid packets during the key exchange process", e);
            }
        } else {
            return false;
        }

    }

    private <T extends Packet> T readPacket() throws IOException, ClosedConnectionException, InvalidPacketException {



        try {
            if (keys != null) {
                crypterManager.setAESkey(keys.getCommunication_key());
                crypterManager.setBase64IV(keys.getCommunication_initialization_vector());

                return Packet.fromJSON(crypterManager.decryptAES(connectionReader.read()));

            } else {
                return connectionReader.readPacket();
            }
        } catch (ImpossibleConversionException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new ClosedConnectionException("An unknown encryption related error while trying to send a Packet", e);
        }
    }

    private <T extends Packet> void writePacket(T packet) throws ClosedConnectionException {
        try {
            if (keys != null) {
                crypterManager.setAESkey(keys.getCommunication_key());
                crypterManager.setBase64IV(keys.getCommunication_initialization_vector());

                connectionWriter.send(crypterManager.encryptAES(packet.toJSON()));

            } else {
                connectionWriter.sendPacket(packet);
            }
        } catch (ImpossibleConversionException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new ClosedConnectionException("An unknown encryption related error while trying to send a Packet", e);
        }
    }


    /**
     * Stop any currently ongoing connection process and eliminate this thread.
     */
    public void shutdown() throws IOException {
        synchronized (lock) {
            isRunning = false;

            if(!streamsTransfered) {
                socket.close();
                connectionWriter.close();
                connectionReader.close();
            } else {
                connectionWriter = null;
                connectionReader = null;
            }
            Thread.currentThread().interrupt();
        }
    }
}
