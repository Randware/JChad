package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.keys.CrypterKey;
import net.jchad.server.model.cryptography.tagUnit.KeyUnit;
import net.jchad.server.model.cryptography.tagUnit.TagUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class CrypterManager {

    private KeyPair keyPair = null;
    private SecretKey secretKey = null;
    private String iv = "";
    private String signature = null;


    public String encryptAES(String plainText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new String(
                CrypterUtil.bytesToBase64(
                        Crypter.encryptAES(
                                plainText.getBytes(StandardCharsets.UTF_8),
                                secretKey,
                                iv,
                                TagUnit.DEFAULT)
                )
        );
    }

    public String decryptAES(String encryptedText, byte[] iv) {
        Crypter.decryptAES(
                encryptedText.getBytes(StandardCharsets.UTF_8),
                secretKey,
                iv,
                TagUnit.DEFAULT
        )
    }

    public CrypterManager() {
        this(CrypterUtil.getRandomByteArr());
    }

    public CrypterManager(byte[] iv) {
        this.iv = iv;
    }

    /**
     * Sets the AES secret key
     * @param secretKey the new secret key
     * @return returns false if the given secret key was null
     *         (if the key is null a random new one gets assigned instead of the provided key)
     */
    public boolean setAESkey(SecretKey secretKey) {
        this.secretKey = secretKey;
        return initAESKey();
    }

    /**
     * Sets the AES secret key
     * @param keyUnit The key unit for the aes key
     */
    public boolean setAESkey(KeyUnit keyUnit) {
        if (keyUnit == null) keyUnit = KeyUnit.DEFAULT;
        this.secretKey = CrypterKey.getAESKey(keyUnit);
        return initAESKey(keyUnit);
    }

    /**
     * Sets the AES secret key
     */
    public boolean setAESkey() {
        return setAESkey(KeyUnit.DEFAULT);
    }

    /**
     * Sets the private key of the key pair
     * @param privateKey The public key that should replace the old one
     * @return returns 'false' if the provided private key is null or other fields of the key pair were null
     *         (the variables with null got replaced to valid ones)
     */
    public boolean setPrivateRSAkey(PrivateKey privateKey) {
        keyPair = new KeyPair(keyPair.getPublic(), privateKey);
        return initKeyPair();
    }

    /**
     * Sets the public key of the key pair
     * @param publicKey The public key that should replace the old one
     * @return returns 'false' if the provided public key is null or other fields of the key pair were null
     *         (the variables with null got replaced to valid ones)
     */
    public boolean setPublicRSAkey(PublicKey publicKey) {
        keyPair = new KeyPair(publicKey, keyPair.getPrivate());
        return initKeyPair();
    }

    /**
     * sets the key pair to the provided one
     * @param keyPair the new key pair
     * @return This returns false if there were some issues with the provided key pair
     *         that got resolved by filling out the keyPair/privateKey/publicKey field
     */
    public boolean setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        return !initKeyPair();
    }

    /**
     * sets the key pair to a newly generated one
     * @return This returns false if there were some issues with the provided key pair
     *         that got resolved by filling out the keyPair/privateKey/publicKey field
     */
    public boolean setKeyPair() {
        this.keyPair = CrypterKey.getRSAKeyPair();
        return !initKeyPair();
    }

    public KeyPair getKeyPair() {
        initKeyPair();
        return keyPair;
    }

    /**
     * Sets the keypair to a valid keypair instead of null
     * @return if some changes where made to the keypair 'true' gets returned
     */
    private boolean initKeyPair() {
        return initKeyPair(1024);
    }

    /**
     * Sets the keypair to a valid keypair if it or its private/public keys gets changed
     * @param keysize the size of the key must be equal or larger than 512
     * @return If the keyPair gets initialized (or its private/public key) this returns 'true', otherwise this will return 'false'
     */
    private boolean initKeyPair(int keysize) {
        boolean gotChanged = false;
        if (keyPair == null) {
            keyPair = CrypterKey.getRSAKeyPair(keysize);
            gotChanged = true;
        }
        if (keyPair.getPublic() == null) {
            keyPair = new KeyPair(CrypterKey.getRSAKeyPair(keysize).getPublic(), keyPair.getPrivate());
            gotChanged = true;
        }
        if (keyPair.getPrivate() == null) {
            keyPair = new KeyPair(keyPair.getPublic(), CrypterKey.getRSAKeyPair(keysize).getPrivate());
            gotChanged = true;
        }

        return gotChanged;
    }

    /**
     * Sets the secret key to a valid one instead of null
     * @param unit The key size of the key
     * @return If the aes key gets initialized this returns 'true', otherwise this will return 'false'
     */
    private boolean initAESKey(KeyUnit unit) {
        if (unit == null) unit = KeyUnit.DEFAULT;
        if (secretKey == null) {
            secretKey = CrypterKey.getAESKey(unit);
            return true;
        }
        return false;
    }
//TODO Implementateeteteteteteteete exception
    public void setInitializationVector(byte[] iv) {
        this.iv = CrypterUtil.stringToBase64(
                CrypterUtil.bytesToString(iv)
        );
    }

    public String getBase64IV() {
        return iv;
    }

    public byte[] getIV() {
        return CrypterUtil.base64ToString(iv).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Sets the secret key to a valid one instead of null
     * @return If the aes key gets initialized this returns 'true', otherwise this will return 'false'
     */
    private boolean initAESKey() {
        return initAESKey(KeyUnit.DEFAULT);
    }


}
