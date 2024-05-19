package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.PacketType;

public final class ServerMessagePacket extends MessagePacket {

    private String username;
    private final Long timestamp;

    public ServerMessagePacket(String message, String chat, String username, long timestamp) {
        super(PacketType.SERVER_MESSAGE, message,  chat);
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

}
