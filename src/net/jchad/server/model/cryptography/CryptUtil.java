package net.jchad.server.model.cryptography;

import java.security.SecureRandom;

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

    public static String toHex(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte bytes : byteArray) {
            stringBuilder.append("%02x".formatted(bytes));
        }
        return stringBuilder.toString();
    }



}
