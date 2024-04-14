package net.jchad.server.model.cryptography;

import net.jchad.server.model.cryptography.keys.CrypterKey;
import net.jchad.server.model.cryptography.tagUnit.KeyUnit;

import javax.crypto.SecretKey;
import java.security.KeyPair;

public class CrypterManager {

    private KeyPair keyPair = null;
    private SecretKey secretKey = null;
    private byte[] iv = new byte[0];


    public CrypterManager() {

    }

    public boolean setAESkey(SecretKey secretKey) {
        if (secretKey == null) return false;
        this.secretKey = secretKey;
        return true;
    }

    public boolean setAESkey(KeyUnit keyUnit) {
        if (keyUnit == null) keyUnit = KeyUnit.DEFAULT;
        try {
            this.secretKey = CrypterKey.getAESKey(keyUnit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setAESkey() {
        return setAESkey(KeyUnit.DEFAULT);
    }

    public boolean setKeyPair(KeyPair keyPair) {
        if (keyPair == null) {
            this.keyPair = CrypterKey.getRSAKeyPair();
            return false;
        } else {
            this.keyPair = keyPair;
            return true;
        }
    }

    public boolean setKeyPair() {
        this.keyPair = CrypterKey.getRSAKeyPair();
        return false;
    }

    public KeyPair getKeyPair() {
        if (keyPair == null) keyPair = CrypterKey.getRSAKeyPair();
        return keyPair;
    }


}
