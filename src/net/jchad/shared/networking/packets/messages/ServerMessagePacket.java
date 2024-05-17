package net.jchad.shared.networking.packets.messages;

import net.jchad.shared.networking.packets.PacketType;

public final class ServerMessagePacket extends MessagePacket {

    private String username;
    private final Long timestamp;
    private final String ip;

    public ServerMessagePacket(String message, boolean encrypted, String chat, String username, long timestamp, String ip) {
        super(PacketType.SERVER_MESSAGE, message, encrypted, chat);
        this.username = username;
        this.timestamp = timestamp;
        this.ip = ip;
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

    public String getIp() {
        return ip;
    }
}
