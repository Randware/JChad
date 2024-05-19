package net.jchad.tests.shared;

import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.CrypterUtil;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.encryption.RSAkeyPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException, InvalidAlgorithmParameterException {
       // User user = new User("Test user", null, );
        CrypterManager m = new CrypterManager();
        String encryptedTestData = m.encryptAES("Hello World!");
        System.out.println(m.getBase64IV());
        System.out.println(m.getAESKey());
        CrypterManager m2 = new CrypterManager();
        m2.setAESkey(m.getAESKey());
        m2.setBase64IV(m.getBase64IV());
        System.out.println(m2.getBase64IV());
        System.out.println(m2.getAESKey());
        System.out.println(m2.decryptAES(encryptedTestData));


        CrypterManager crypterManager = new CrypterManager();
        crypterManager.setAESkey("0O7ViCSSk2kOglnSAVYTRX9A+T6B/JN9ZEBA8RT7bBw=");
        crypterManager.setBase64IV("Bi3vv73vv70fPS7vv73vv71YAnrvv73vv707BA==");

        System.out.println(crypterManager.decryptAES("+BWfFVBLdLTuQcPnVfJ2jOc0fkbvmxynF0ZY+CR0MMiTlbAhcp2UlFZM+/kMkkNHL1c="));

        System.out.println(new PasswordResponsePacket("n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=").toJSON());
        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        //System.out.println(new RSAkeyPacket(publicKey).toJSON());
    }
}
