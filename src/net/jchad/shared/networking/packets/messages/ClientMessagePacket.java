package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.PacketType;

public final class ClientMessagePacket extends MessagePacket {
    public ClientMessagePacket(String message,  String chat) {
        super(PacketType.CLIENT_MESSAGE, message, chat);

    }

}
