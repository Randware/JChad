package net.jchad.server.model.server.util;

import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.encryption.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSAHelperThread extends HelperThread{
    private final CrypterManager crypterManager;

    public RSAHelperThread(ServerThread serverThread, CrypterManager crypterManager) {
        super(serverThread);
        this.crypterManager = crypterManager;
    }

    public RSAHelperThread exchangeRSAKeys() {
        getServerThread().getMessageHandler().handleDebug("Trying to exchange public RSA keys with %s".formatted(getServerThread().getRemoteAddress()));
        writePacket(new KeyExchangeStartPacket());
        writePacket(new PublicRSAkeyPacket(crypterManager.getPublicKey()));
        PublicRSAkeyPacket remotePublicRSA = readJSON(PublicRSAkeyPacket.class, PacketType.RSA_KEY_EXCHANGE);
        crypterManager.setRemotePublicKey(remotePublicRSA.getPublic_key());
        writePacket(new KeyExchangeEndPacket());
        getServerThread().getMessageHandler().handleDebug("Public RSA keys got exchanged successfully %s".formatted(getServerThread().getRemoteAddress()));
        return this;
    }

    public RSAHelperThread sendAESkeys() {
        /**
         * Why don't assign the values immediately?
         * If the values get assigned now the {@link ServerThread#write(String)} will encrypt the packets with the newly set AES keys
         */
        String messageAESkey =  CrypterManager.generateAESkey();
        String communicationAESkey = CrypterManager.generateAESkey();
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            System.out.println(messageAESkey + ":" + encoder.encodeToString(getServerThread().getMessageIV()));
            String encryptedMessageAESkey = crypterManager.encryptRSA(new MessageEncryptionKeyPacket(messageAESkey).toJSON()); //Format: AES_KEY:IV
            getServerThread().write(encryptedMessageAESkey);
            String encryptedCommunicationsAESkey = crypterManager.encryptRSA(new CommunicationEncryptionKeyPacket(communicationAESkey).toJSON()); //Format: AES_KEY:IV
            getServerThread().write(encryptedCommunicationsAESkey);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
            getServerThread().getMessageHandler().handleDebug("An unsuspected error occurred while trying to encrypt with the client's RSA keys", e);
            writePacket(new PublicRSAkeyErrorPacket("An unsuspected error occurred"));
            getServerThread().close("An unknown error occurred while trying to encrypt data with the client provided RSA key");
        } catch (InvalidKeyException e) {
            getServerThread().getMessageHandler().handleDebug("An unsuspected error occurred while trying to encrypt with the client's RSA keys", e);
            writePacket(new PublicRSAkeyErrorPacket("The provided RSA key was invalid! Make sure it is Base64 encoded"));
            getServerThread().close("The client's RSA key is not valid");
        }

        getServerThread().setMessageAESkey(messageAESkey);
        getServerThread().setCommunicationsAESkey(communicationAESkey);
        return this;
    }
}
