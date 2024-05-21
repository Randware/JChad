package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public final class ClientMessagePacket implements Packet {
    private final PacketType packet_type = PacketType.CLIENT_MESSAGE;
    private final String message;
    private final String chat;

    public ClientMessagePacket(String message, String chat) {
        this.message = message;
        this.chat = chat;
    }

    public String getMessage() {
        return message;
    }

    public String getChat() {
        return chat;
    }
}
