package net.jchad.server.model.cryptography;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * This class provides <b><u>Thread-safe</u></b> utility for some cryptographic related tasks
 */
public class CryptUtil {
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Return a random Byte-Array with the given length.
     * @param length The length of the array that gets returned
     * @return The random array
     */
    public static byte[] getRandomByteArr(int length) {
        if (length < 1) length = 16;
        byte[] randArray = new byte[length];
        synchronized (secureRandom) {
            secureRandom.nextBytes(randArray);
            return randArray;
        }
    }

    /**
     * Return a random Byte-Array with the length of 16
     * @return The randomly generated array
     */
    public static byte[] getRandomByteArr() {
        return getRandomByteArr(16);
    }

    public static String bytesToHex(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte bytes : byteArray) {
            stringBuilder.append("%02x".formatted(bytes));
        }
        return stringBuilder.toString();
    }

    /**
     * Parses a hex-string to a byte array
     *
     * @param hexString The hex string that gets converted
     * @return the converted byte array OR an empty array if the string contains non-hexadecimal characters
     */
    public static byte[] hexToBytes(String hexString) {
        HexFormat hex = HexFormat.of();
        try {
            return hex.parseHex(hexString);
        } catch (IllegalArgumentException e) {
            return new byte[0];
        }
    }

    /**
     * Converts a byte array to a base64 byte array
     */
    public static byte[] bytesToBase64(byte[] byteArray) {
        return Base64.getEncoder().encode(byteArray);
    }

    /**
     * Converts a base64 encoded byte array into a normal one
     * Return an empty array if the given base64 array is not a base64 encoded array
     */
    public static byte[] base64ToBytes(byte[] base64Array) {
        try {
            return Base64.getDecoder().decode(base64Array);
        } catch (IllegalArgumentException e) {
            return new byte[0];
        }
    }
    /**
     * Converts a normal String to a base64 encoded String
     */
    public static String stringToBase64(String str) {
        return bytesToString(Base64.getEncoder().encode(str.getBytes()));
    }

    /**
     * Converts a base64 encoded String into a normal one
     * If the given String is not convertable null gets returned
     * @return the encoded string or null if is not decodaeble
     */
    public static String base64ToString(String base64EncodedString) {
        try {
            return bytesToString(Base64.getDecoder().decode(base64EncodedString));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Converts a byte array to an UTF-8 string
     */

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
