package net.jchad.tests.shared;

import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.networking.packets.encryption.RSAkeyPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
       // User user = new User("Test user", null, );
        CrypterManager crypterManager = new CrypterManager();
        crypterManager.setKeyPair(4096);
        String privateKey = crypterManager.getPrivateKey();
        String publicKey = crypterManager.getPublicKey();
        crypterManager.setRemotePublicKey(crypterManager.getPublicKey());
        System.out.println(crypterManager.encryptRSA("test"));

        System.out.println("Private: " + privateKey);
        System.out.println("Public: " + publicKey);
        System.out.println(new PasswordResponsePacket("n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=").toJSON());
        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        System.out.println(new RSAkeyPacket(publicKey).toJSON());
    }
}
