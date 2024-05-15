package net.jchad.shared.networking.packets;

public final class ClientMessagePacket extends MessagePacket {
    public ClientMessagePacket(String message, boolean encrypted, String chat) {
        super(PacketType.CLIENT_MESSAGE, message, encrypted, chat);

    }

}
