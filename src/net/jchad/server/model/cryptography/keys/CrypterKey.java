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
public class CrypterKey {


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

    /**
     * Generates an AES encryption/decryption key from a password and a salt
     * @param password The password
     * @param salt The salt
     * @return The key generated with the key and the password
     */
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

    /**
     * Generates an RSA keypair with the key size of 1024
     * @return The RSA key pair
     */
    public static KeyPair getRSAKeyPair() {
        return getRSAKeyPair(1024);
    }

    /**
     * Converts the given byte array into an AES SecretKey.
     * Returns null if the byte array is invalid
     * @param secretKeyBytes The byte array that was obtained by doing: aesKey.getEncoded();
     * @return The converted AES Key or null if the given byte array is not a SecretKey
     */
    public static SecretKey getAESkeyFromBytes(byte[] secretKeyBytes) {
        return new SecretKeySpec(secretKeyBytes, "AES");
    }

    /**
     * Converts an AES key to a byte array
     * @param key the AES key that should get converted
     * @return the byte array representation of the AES key
     */
    public static byte[] getBytesFromAESkey(Key key) {
        return key.getEncoded();
    }

    /**
     * Converts a byte array to a Private key
     * Returns null if the given byte array is not convertable into a Private Key
     *
     * @param privateKeyBytes the byte array that should be converted
     * @return the converted Private key or null if the byte array is not a Private key
     */
    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes) {
        try {

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Converts a byte array to a Public Key
     * Returns null if the given byte array is not convertable into a Public Key
     *
     * @param publicKeyBytes the byte array that should be converted
     * @return the converted Public key or null if the byte array is not a Public Key
     */
    public static PublicKey gePublicKeyFromBytes(byte[] publicKeyBytes) {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception e) {
            return null;
        }
    }
}
