package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.crypterUnits.TagUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

public class CrypterTest {
    public static void main(String[] args) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ImpossibleConversionException {
        /*String text = "Hello, World";
        SecretKey key = CrypterKey.getAESKey();
        SecretKey key2 = CrypterKey.getAESKey();
        byte[] iv = CrypterUtil.getRandomByteArr(16);
        byte[] iv2 = CrypterUtil.getRandomByteArr(16);
        byte[] encryptedText = CrypterUtil.bytesToBase64(Crypter.encryptAES(text.getBytes(StandardCharsets.UTF_8), key, iv, TagUnit.LENGTH_128));
        String decryptedText = Crypter.decryptAES(CrypterUtil.base64ToBytes(encryptedText), key, iv, TagUnit.LENGTH_128);
        System.out.println("Clear Text: " + text);
        System.out.println("Encrypted Text: " + CrypterUtil.base64ToString(new String(encryptedText, StandardCharsets.UTF_8)));
        System.out.println("Decrypted Text: " + decryptedText);*/
        /*String hw = "Hello World! This is JChad written by Randware";
        String base64 = CrypterUtil.stringToBase64(hw);
        String decoded = CrypterUtil.base64ToString(base64);
        System.out.println(base64);
        System.out.println(decoded);*/

        /*KeyPair keyPair = CrypterKey.getRSAKeyPair();
        String plainText = "Hello World";

        byte[] encryptedArr = Crypter.encryptRSA(CrypterUtil.stringToBase64Byt(plainText), keyPair.getPublic());
        byte[] decryptedText = CrypterUtil.base64ToBytes(Crypter.decryptRSA(encryptedArr, keyPair.getPrivate()));
        System.out.println(CrypterUtil.bytesToString(decryptedText));*/

        String plainTextAES = "Hello World I love AES!";
        CrypterManager crypterManager = new CrypterManager();
        String encryptedString = crypterManager.encryptAES(plainTextAES);
        String decryptedString = crypterManager.decryptAES(encryptedString);
        System.out.println(decryptedString); //Output: Helle World I love AES!



    }
}
