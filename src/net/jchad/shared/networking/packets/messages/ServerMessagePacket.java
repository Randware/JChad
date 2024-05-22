package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

public final class ServerMessagePacket implements Packet {

    private final PacketType packet_type = PacketType.SERVER_MESSAGE;
    private final String message;
    private final String chat;
    private String username;
    private final Long timestamp;

    public ServerMessagePacket(String message, String chat, String username, long timestamp) {
        this.message = message;
        this.chat = chat;
        this.username = username;
        this.timestamp = timestamp;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getChat() {
        return chat;
    }

    @Override
    public String toString() {
        return "ServerMessagePacket{" +
                "packet_type=" + packet_type +
                ", message='" + message + '\'' +
                ", chat='" + chat + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
