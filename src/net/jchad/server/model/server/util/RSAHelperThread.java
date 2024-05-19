package net.jchad.server.model.server.util;

import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.CrypterUtil;
import net.jchad.shared.cryptography.keys.CrypterKey;
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
        //writePacket(new RSAkeyPacket(crypterManager.getPublicKey())); //Server's public key is never needed
        RSAkeyPacket remotePublicRSA = readJSON(RSAkeyPacket.class, PacketType.RSA_KEY_EXCHANGE);
        crypterManager.setRemotePublicKey(remotePublicRSA.getPublic_key());
        getServerThread().getMessageHandler().handleDebug("Public RSA keys got exchanged successfully with %s".formatted(getServerThread().getRemoteAddress()));
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
            String aesKeys = new AESencryptionKeysPacket(messageAESkey, encoder.encodeToString(getServerThread().getMessageIV()),
                    communicationAESkey, encoder.encodeToString(getServerThread().getCommunicationsIV())).toJSON();
            String encryptedAesKeys = crypterManager.encryptRSA(aesKeys); //Format: AES_KEY:IV
            getServerThread().write(encryptedAesKeys);
        } catch (NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException e) {
            getServerThread().getMessageHandler().handleDebug("An unsuspected error occurred while trying to encrypt with the client's RSA keys", e);
            writePacket(new RSAkeyErrorPacket("An unsuspected error occurred"));
            getServerThread().close("An unknown error occurred while trying to encrypt data with the client provided RSA key");
        } catch (IllegalBlockSizeException e) {
            getServerThread().getMessageHandler().handleDebug("The client provide RSA key is to short (The key size has to be 4096) or invalid", e);
            writePacket(new RSAkeyErrorPacket("Data could not be encrypted, because the RSA key is to short. Make sure that the key size is 4096"));
            getServerThread().close("Data could not be encrypted, because the client provided RSA key is to short");
        } catch (InvalidKeyException e) {
            getServerThread().getMessageHandler().handleDebug("An unsuspected error occurred while trying to encrypt with the client's RSA keys", e);
            writePacket(new RSAkeyErrorPacket("The provided RSA key was invalid! Make sure it is Base64 encoded"));
            getServerThread().close("The client's RSA key is not valid");
        }

        getServerThread().setMessageAESkey(messageAESkey);
        getServerThread().setCommunicationsAESkey(communicationAESkey);
        writePacket(new KeyExchangeEndPacket());
        return this;
    }
}
