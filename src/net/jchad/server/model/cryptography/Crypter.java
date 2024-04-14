package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.tagUnit.TagUnit;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class Crypter {

    /**
     * Encrypts the input using Advanced Encryption Standard (GCM MODE)
     * @param plainText The text that should get encrypted
     * @param secretKey The secret key
     * @param iv
     * @param tagUnitLength Length of the TagUnit (Default should be 128)
     * @return The encrypted byte-array
     */
    public static byte[] encryptAES(byte[] plainText, SecretKey secretKey, byte[] iv, TagUnit tagUnitLength) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (tagUnitLength == null) tagUnitLength = TagUnit.DEFAULT;
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(tagUnitLength.getValue(), iv));
            return cipher.doFinal(plainText);
    }

    /**
     * Decrypts the input using Advanced Encryption Standard (GCM MODE)
     * @param crypticText The text that should get decrypted
     * @param secretKey The secret key
     * @param iv
     * @param tagUnitLength Length of the TagUnit (Default 128)
     * @return the decrypted string OR null if the key isn't the right one!
     */
    public static String decryptAES(byte[] crypticText, SecretKey secretKey, byte[] iv, TagUnit tagUnitLength) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        if (tagUnitLength == null) tagUnitLength = TagUnit.DEFAULT;

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new  GCMParameterSpec(tagUnitLength.getValue(), iv));
        return new String(cipher.doFinal(crypticText), StandardCharsets.UTF_8);
        /*
        byte[] plainText = cipher.doFinal(crypticText);
        return new String(plainText, UTF_8);
         */

    }

    public static byte[] encryptRSA(byte[] plainText, Key publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainText);
    }

    public static byte[] decryptRSA(byte[] encryptedText, Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedText);


    }



    public static byte[] sign(String plainText, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(privateKey);
            privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
            return privateSignature.sign();

    }

    public static boolean verify(String plainText, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(publicKey);
            publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
            return publicSignature.verify(signature);
    }
}
