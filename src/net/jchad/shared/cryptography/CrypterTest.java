package net.jchad.shared.cryptography;

import net.jchad.shared.cryptography.keys.CrypterKey;
import net.jchad.shared.networking.packets.encryption.AESencryptionKeysPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

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

        //Public key: MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCetybIEf/3nz2V0fU6+FKzzSkijgQ2dURPCAUjn1IORE/DrpDO8ZpG0cUbNk4LVAPXu1qT/eUzOFxuiZtdb7sWcge74t60oZGus9XY2b0eGelMZiq0943EXcKiUy5+dtYbLnebvmERHWU5bxO3D2utMMntqFuGlsPMnXWNYklI4QIDAQAB
        //Private key: MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ63JsgR//efPZXR9Tr4UrPNKSKOBDZ1RE8IBSOfUg5ET8OukM7xmkbRxRs2TgtUA9e7WpP95TM4XG6Jm11vuxZyB7vi3rShka6z1djZvR4Z6UxmKrT3jcRdwqJTLn521hsud5u+YREdZTlvE7cPa60wye2oW4aWw8yddY1iSUjhAgMBAAECgYAZWqY4F0IF51sR9Cah6zng7bD4y2mKXozRODWD9YdI7qo0Vw5cdLWBwzBi0rfAcQUlIRGM6yYCWupPIS3fzQG8iWMrsD70eoOvxV2wmKlV5tsyqjZ+lc4yZvEvR25XT7ZnRMwW7YZDop2nTOzkzTXo2QN9iO4YZocY2qZ/ho89PQJBANdpQLCC6rR5ybQdhiVJgAev4UX+bOMUR7lK/F2sLt0hc8Bx5AHlgEQb5iYruP612rsZvEGs3W14qafLVtl8ZjUCQQC8nxQWocdcAADl4wfHnjTHpcw9wCUhQzDlsUV9pt+ytAM51PELTpNHVAA8YCpF4I7CMSq1i3J2yjN6NrBOhf19AkA35lFqPN8JbJ1iR1MbdAJsfEDaeBbrqsSeGg55hIg4zEiMhUGlGFh71D2aZDhCqRCVAy2uYVyo3uov5/2mvLiNAkEArlZiXOC+wRwHuHN011407msiMdkM+IeABG0rC45XqHaVnLhi6s5/dif/584ChH+fs4F6Nj5jV8RgfA9cOdfCiQJBAMh5WNBZiobtIkHr1Nkx/wizlJ8JzkFzcLddcGJg7H34GxJ3d4Hs6Pbs9T8vr0pkwcJM1iVk+ggfe2PSBvweaF4=

        /*String plainTextAES = "Hello World I love AES!";
        CrypterManager crypterManager = new CrypterManager();
        String encryptedString = crypterManager.encryptRSA(plainTextAES);
        System.out.println(encryptedString);
        System.out.println("Public key: " + crypterManager.getPublicKey());
        System.out.println("Private key: " + crypterManager.getPrivateKey());
        String signature = crypterManager.sign(encryptedString);

        System.out.println("The given signature and signed text " + (crypterManager.verify(signature, encryptedString) ? "match!" : "do not match!"));
        String decryptedString = crypterManager.decryptRSA(encryptedString);
        System.out.println(decryptedString); //Output: Helle World I love AES!*/

        /*CrypterManager crypterManager = new CrypterManager();
        crypterManager.setPrivateRSAkey(CrypterKey.getPrivateKeyFromBytes(Base64.getDecoder().decode("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIVt6vQi/CTlJpn64t4VSOD64P25R94p7DpedMwphrQ7vAEvYNlZoasAsPxlV8i6XEvcmVC3nn7YbqT9z1DT4dpyj3IcwDjUv0EBGyv2+sXG3TViKF3OxrG2MOq2VZOQ2E9kCmxGPoBM2vAB90xG/SH1ACMxH0rUJt7otQLbe3YrAgMBAAECgYAT1y+SpFMhugYJQTJpUKPuJGVpWnA9MQxZ9Oq24yPkeroZjlh/PrdDV3kkhtNMsKgsdaiXRT6RtmWO2NjyOBeDbi8qnksRC7a6pRbab2mmNn55FfFULQuHwwuJOI0HkGJ+BedwmPhbDA/LGpYHRww4MgyBZIpM4nM/VvTx0cLeRQJBAMdcM9rvgwxgyYqUGgvkY+JOygF22pM62j2mWlrpcHZJH9VOpHQVOQxx4okyM0DOYSNentk6BBBqoRZgkiqJpN0CQQCrVnVK6IyoxvUAKXeMOe4uUgUA6bVlu+KBpQF/TeOkt7D3xvZoY3twIOvRxGw2i3OefOGjVhsJc2/Yq7sLuPKnAkBsrsdWQ9ydkuZMthC9CvWpGmrmAXWHbj4r/dSkWz8EPTktgIIHBsrjDfMB+GwYmGo8bgkLQ6WTjY3kNSF6Ly/BAkEAguFia7xEZlDEqkECzx0Zcky5Ab+FD5d8KOhwwlwvPo4LItrbM1Z63mZnfS98MhZIVZKzkd+ee5OwpmRYV1YKFQJAZwozi3F2TuFsCCHVUVwn6Gdc0KC7swYurDQ8ElS4lN4565p4X8AHD4la8U9GUxB3zlOYIf0IIQ8LTRpzzTnh8Q==")));
        crypterManager.setPublicRSAkey(CrypterKey.getPublicKeyFromBytes(Base64.getDecoder().decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxCOt7ft77nq9fewOaeS0ntFehfYClHF7tAdZ4iI5+692KU/rBrB5O8bDnhv24XBdY+bDB/jfl6wHjBA91XbTGi7nlkFJHL3ap5Id6QL78gyPv2DKeQwPEY6AvBodP/AZ0m0zLFN/np5vf/wT1AuoKP3lg1yZ2Z9i/5QoeJ0elyQIDAQAB")));

        System.out.println(crypterManager.decryptRSA("YEBpS0vsOqEHcQwDiYpgupZgcDZlF0p21N05xZfWPSOCQqZoUM7UuCUHE8N5fv8i9IkZKkA1+Q25TRiZiBLSTepYWKXQSCXZdjyOspHTBg61uD6ZpoWQxEJXbCzAwHTAhPRf9dMu2wsrKjrCqRS2XVYG8/UqgTxfV3UDDJx3Cdg="));*/

        CrypterManager manager = new CrypterManager();
        manager.setKeyPair(4096);
        CrypterKey.getAESKey();
        manager.setRemotePublicKey(manager.getPublicKey());
        String dataToEncrypt = new AESencryptionKeysPacket(manager.getAESKey(),manager.getBase64IV(),manager.getAESKey(),manager.getBase64IV()).toJSON();
        System.out.println(dataToEncrypt);
        //dataToEncrypt = Base64.getEncoder().encodeToString(dataToEncrypt.getBytes());
        System.out.println(manager.encryptRSA(dataToEncrypt));

    }
}
