package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.keys.CreateKey;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class CrypterTest {
    public static void main(String[] args) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        /*String text = "Hello, World";
        SecretKey key = CreateKey.getAESKey();
        SecretKey key2 = CreateKey.getAESKey();
        byte[] iv = CryptUtil.getRandomByteArr(16);
        byte[] iv2 = CryptUtil.getRandomByteArr(16);
        byte[] encryptedText = CryptUtil.bytesToBase64(Crypter.encryptAES(text.getBytes(StandardCharsets.UTF_8), key, iv, TagUnit.LENGTH_128));
        String decryptedText = Crypter.decryptAES(CryptUtil.base64ToBytes(encryptedText), key, iv, TagUnit.LENGTH_128);
        System.out.println("Clear Text: " + text);
        System.out.println("Encrypted Text: " + CryptUtil.base64ToString(new String(encryptedText, StandardCharsets.UTF_8)));
        System.out.println("Decrypted Text: " + decryptedText);*/
        /*String hw = "Hello World! This is JChad written by Randware";
        String base64 = CryptUtil.stringToBase64(hw);
        String decoded = CryptUtil.base64ToString(base64);
        System.out.println(base64);
        System.out.println(decoded);*/


        String text = "Hello World";

        KeyPair keyPairServer = CreateKey.getRSAKeyPair();
        KeyPair keyPairClient = CreateKey.getRSAKeyPair();

        PrivateKey clientKeyPrivate = keyPairClient.getPrivate();
        PublicKey clientKeyPublic = keyPairClient.getPublic();

        byte[] str = Crypter.sign(text, clientKeyPrivate);

        System.out.println(Arrays.toString(str));
        System.out.println(Crypter.verify(text,str,clientKeyPublic));


    }
}
