package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.keys.CrypterKey;

import java.security.*;
import java.util.Arrays;

public class CrypterTest {
    public static void main(String[] args) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
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


        byte[] text = "Hello World".getBytes();

        KeyPair keyPairServer = CrypterKey.getRSAKeyPair();
        KeyPair keyPairClient = CrypterKey.getRSAKeyPair();

        PrivateKey clientKeyPrivate = keyPairClient.getPrivate();
        PublicKey clientKeyPublic = keyPairClient.getPublic();

        byte[] str = Crypter.sign(text, clientKeyPrivate);

        System.out.println(Arrays.toString(str));
        System.out.println(Crypter.verify(text,str,clientKeyPublic));
        System.out.println("----");
        System.out.println(CrypterKey.getAESkeyFromBytes(new byte[]{21,125,125}));
    }
}
