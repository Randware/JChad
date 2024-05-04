package net.jchad.server.model.chats;

import net.jchad.shared.networking.ip.IPAddress;

public class ChatMessage {
    private long id;
    private long timestamp;
    private String senderName;
    private IPAddress senderIP;

    public ChatMessage(long id, long timestamp, String senderName, IPAddress senderIP) {
        this.id = id;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderIP = senderIP;
    }

    public ChatMessage(long id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
        this.senderName = null;
        this.senderIP = null;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public IPAddress getSenderIP() {
        return senderIP;
    }
}
