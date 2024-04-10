package net.jchad.server.model.cryptography.keys;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * This class provides <b><u>Thread-safe</u></b> key generation
 */
public class CreateKey {


    /**
     * Generates a random AES-Key
     * @param keyLength The size of the key 64,128,256...
     * @return the AES secret Key
     */
    public static SecretKey getAESKey(int keyLength) {
        if (keyLength < 1) keyLength = 256;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keyLength, SecureRandom.getInstanceStrong());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random AES-Key with the size of 256
     * @return The AES-Key
     */
    public static SecretKey getAESKey() {
        return getAESKey(256);
    }

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt, int iterations, int keyLength) {
        if (iterations < 1) iterations = 65535;
        if (keyLength < 1) keyLength = 256;
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password,salt,iterations,keyLength);
            SecretKey secretKey = new SecretKeySpec(keyFactory.generateSecret(spec).getEncoded(), "AES");
            return secretKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) {
        return getAESKeyFromPassword(password,salt,-1,-1);
    }
}
