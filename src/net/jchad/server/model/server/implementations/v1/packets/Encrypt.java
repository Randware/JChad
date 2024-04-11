package net.jchad.server.model.server.implementations.v1.packets;

public enum Encrypt implements SubType, PacketString<Encrypt>{
    AES_ERROR,
    RSA_ERROR,
    AES_ENCRYPTED,
    AES_ENCRYPTED_ERROR,
    AES_DECRYPTED_ERROR,
    RSA_ENCRYPTED,
    RSA_ENCRYPTED_ERROR,
    RSA_DECRYPTED_ERROR,
    ENCRYPTION_STATUS,
    RSA_PUBLIC_KEY_INFO,
    RSA_PUBLIC_KEY_RESPONSE,
    AES_KEY_REQUEST,
    ARS_KEY_RESPONSE;


    @Override
    public Encrypt stringToValue(String stringEnum) {
        return valueOf(enumName());
    }

    @Override
    public PacketTypes getParent() {
        return PacketTypes.ENCRYPT;
    }

    @Override
    public Object[] getSpecificFields() {
        return new Object[0];
    }
}
