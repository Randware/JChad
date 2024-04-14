package net.jchad.server.model.cryptography.keys;

import net.jchad.server.model.cryptography.tagUnit.KeyUnit;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * This class provides <b><u>Thread-safe</u></b> key generation
 */
public class CreateKey {


    /**
     * Generates a random AES-KeyUnit
     * @param key The length of the key
     * @return the AES secret KeyUnit
     */
    public static SecretKey getAESKey(KeyUnit key) {
        if (key == null) key = KeyUnit.DEFAULT;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(key.value, SecureRandom.getInstanceStrong());
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random AES-KeyUnit with the size of 256
     * @return The AES-KeyUnit
     */
    public static SecretKey getAESKey() {
        return getAESKey(KeyUnit.DEFAULT);
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
     * @return The KeyUnit-pair
     */
    public static KeyPair getRSAKeyPair(int keysize) {
        if (keysize < 512) keysize = 2048;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keysize);
            return generator.generateKeyPair();
        } catch (Exception e) {
            return null;
        }
    }

    public static KeyPair getRSAKeyPair() {
        return getRSAKeyPair(1024);
    }


    public static SecretKey keyFromBytes(byte[] bytesOfKey) {
        try {
            KeyFactory facotry = KeyFactory.getInstance("RSA");
            SecretKey key = new SecretKeySpec(bytesOfKey, 0, bytesOfKey.length, "RSA");
            return key;
        } catch (Exception e) {
            return null ;
        }
    }



    public static SecretKey getSecretKeyFromBytes(byte[] secretKeyBytes) {
        return new SecretKeySpec(secretKeyBytes, "AES");
    }

    public static byte[] getBytesFromSecretKey(Key key) {
        return key.getEncoded();
    }

    public static PrivateKey gePrivateKeyFromBytes(byte[] privateKeyBytes) {
        try {

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (Exception e) {
            return null;
        }
    }



    public static PublicKey gePublicKeyFromBytes(byte[] publicKeyBytes) {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception e) {
            return null;
        }
    }
}
