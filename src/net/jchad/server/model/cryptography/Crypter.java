package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.tagUnit.Tag;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Crypter {

    /**
     * Encrypts the input using Advanced Encryption Standard (GCM MODE)
     * @param plainText The text that should get encrypted
     * @param secretKey The secret key
     * @param iv
     * @param tagLength Length of the Tag (Default should be 128)
     * @return The encrypted byte-array
     */
    public static byte[] encryptAES(byte[] plainText, SecretKey secretKey, byte[] iv, Tag tagLength) {
        if (tagLength == null) tagLength = Tag.DEFAULT;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(tagLength.getValue(), iv));
            return cipher.doFinal(plainText);
        } catch (InvalidKeyException e) {
            return new byte[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypts the input using Advanced Encryption Standard (GCM MODE)
     * @param crypticText The text that should get decrypted
     * @param secretKey The secret key
     * @param iv
     * @param tagLength Length of the Tag (Default 128)
     * @return the decrypted string OR null if the key isn't the right one!
     */
    public static String decryptAES(byte[] crypticText, SecretKey secretKey, byte[] iv, Tag tagLength) {
        if (tagLength == null) tagLength = Tag.DEFAULT;
        try {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new  GCMParameterSpec(tagLength.getValue(), iv));
        return new String(cipher.doFinal(crypticText), StandardCharsets.UTF_8);
        /*
        byte[] plainText = cipher.doFinal(crypticText);
        return new String(plainText, UTF_8);
         */
        } catch (AEADBadTagException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptRSA(byte[] plainText, Key publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainText);
        } catch (InvalidKeyException e) {
            return new byte[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptRSA(byte[] encryptedText, Key privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedText);
        } catch (InvalidKeyException e) {
            return new byte[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
