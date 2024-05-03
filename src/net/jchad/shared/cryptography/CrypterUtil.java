package net.jchad.shared.cryptography;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * This class provides <b><u>Thread-safe</u></b> utility for some cryptographic related tasks
 */
public class CrypterUtil {
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
     * Encodes the given Base64 byte array
     * @param base64Array the array that gets decoded
     * @return The decoded base64 array
     * @throws ImpossibleConversionException If the provided array is not a base64 byte array
     */
    public static byte[] base64ToBytes(byte[] base64Array) throws ImpossibleConversionException {
        try {
            return Base64.getDecoder().decode(base64Array);
        } catch (IllegalArgumentException e) {
            throw new ImpossibleConversionException(e);
        }
    }
    /**
     * Converts a normal String to a base64 encoded String
     */
    public static String stringToBase64(String str) {
        return bytesToString(Base64.getEncoder().encode(str.getBytes()));
    }

    /**
     * Encodes the given Base64 string
     * @param base64EncodedString the encoded base64 string that gets decoded
     * @return The decoded base64 string
     * @throws ImpossibleConversionException If the provided String is not convertable
     */
    public static String base64ToString(String base64EncodedString) throws ImpossibleConversionException {
        try {
            return bytesToString(Base64.getDecoder().decode(base64EncodedString.getBytes()));
        } catch (IllegalArgumentException e) {
            throw new ImpossibleConversionException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Converts a byte-Array to a base64 encoded String
     * @param bytes the bytes to convert
     * @return the base64 encoded String
     */
    public static String bytesToBase64Str(byte[] bytes){
        return bytesToString((bytesToBase64(bytes)));
    }

    /**
     * converts a string to a base64 encoded byte array
     * @param string The string that gets converted
     * @return the base64 encoded byte array
     */
    public static byte[] stringToBase64Byt(String string) {
        return bytesToBase64(string.getBytes());
    }

    /**
     * converts a base64 encoded byte array to an encoded String
     * @param base64EncodedBytes the encoded byte array
     * @return the decoded string
     * @throws ImpossibleConversionException if the given byte array can not be decoded into base64
     */
    public static String base64BytToString(byte[] base64EncodedBytes) throws ImpossibleConversionException {
        return bytesToString(base64ToBytes(base64EncodedBytes));
    }

    /**
     * decodes a base64 encoded string into a byte array
     * @param base64EncodedString The base64 encoded String
     * @return the encoded byte array
     * @throws ImpossibleConversionException If the given string is not convertable into base64 this exception gets thrown
     */
    public static byte[] base64StrToBytes(String base64EncodedString) throws ImpossibleConversionException {
        return base64ToBytes(base64EncodedString.getBytes());
    }

    /**
     * Converts a byte array to a string
     */
    public static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }

    /**
     * Checks is the given string can be {@link java.util.Base64 Base64} encoded
     * @param stringToTest The String that gets tested
     * @return returns <b>true</b> if the provided string is decodable in base64
     */
    public static boolean isBase64(String stringToTest) {
        return Pattern.matches(stringToTest, "^[A-Za-z0-9+/\\r\\n]+={0,2}$");
    }

}
