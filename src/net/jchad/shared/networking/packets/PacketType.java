package net.jchad.shared.networking.packets;

public enum PacketType {
    BANNED ,
    NOT_WHITELISTED,
    CONNECTION_CLOSED,
    AES_ENCRYPTION_KEY,
    RSA_PUBLIC_KEY;



    /**
     * Gets a more readable form of the enum:
     * NOT_WHITELISTED => Not whitelisted
     * BANNED          => Banned
     * @return
     */
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", "");
    }
}
