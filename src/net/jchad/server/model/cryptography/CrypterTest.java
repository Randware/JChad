package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.keys.CreateKey;
import net.jchad.server.model.cryptography.tagUnit.Tag;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class CrypterTest {
    public static void main(String[] args) {
        String text = "Hello, World";
        SecretKey key = CreateKey.getAESKey();
        SecretKey key2 = CreateKey.getAESKey();
        byte[] iv = CryptUtil.getRandomByteArr(16);
        byte[] iv2 = CryptUtil.getRandomByteArr(16);
        byte[] encryptedText = Crypter.encrypt(text.getBytes(StandardCharsets.UTF_8), key, iv, Tag.LENGTH_128);
        String decryptedText = Crypter.decrypt(encryptedText, key, iv, Tag.LENGTH_128);
        System.out.println("Clear Text: " + text);
        System.out.println("Encrypted Text: " + CryptUtil.toHex(encryptedText));
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
