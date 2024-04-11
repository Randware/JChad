package net.jchad.server.model.cryptography.keys;

import net.jchad.server.model.cryptography.tagUnit.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * This class provides <b><u>Thread-safe</u></b> key generation
 */
public class CreateKey {


    /**
     * Generates a random AES-Key
     * @param key The length of the key
     * @return the AES secret Key
     */
    public static SecretKey getAESKey(Key key) {
        if (key == null) key = Key.DEFAULT;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(key.value, SecureRandom.getInstanceStrong());
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
        return getAESKey(Key.DEFAULT);
    }

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt, int iterations, int keyLength) {
        if (iterations < 1) iterations = 65535;
        if (keyLength < 8) keyLength = 256;
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

    /**
     * Returns a RSA key-pair
     * @param keysize The size of the keys (Has to be larger than 511)
     * @return The Key-pair
     */
    public static KeyPair getRSAKeyPair(int keysize) {
        if (keysize < 512) keysize = 2048;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keysize);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static SecretKey keyFromBytes(byte[] bytesOfKey) {
        try {
            KeyFactory facotry = KeyFactory.getInstance("RSA");
            SecretKey key = new SecretKeySpec(bytesOfKey, 0, bytesOfKey.length, "RSA");
            return key;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
