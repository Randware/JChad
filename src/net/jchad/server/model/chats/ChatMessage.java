package net.jchad.server.model.chats;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ChatMessage {
    private int id;
    private final String content;
    private final boolean encrypted;
    private final long timestamp;
    private final String senderName;
    private final String senderIP;

    public ChatMessage(String content, boolean encrypted, long timestamp, String senderName, String senderIP) {
        this.content = content;
        this.encrypted = encrypted;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderIP = senderIP;
    }

    protected ChatMessage appendID(int id) {
        this.id = id;

        return this;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderIP() {
        return senderIP;
    }

    public LocalDateTime getTimestampAsLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", encrypted=" + encrypted +
                ", timestamp=" + timestamp +
                ", senderName='" + senderName + '\'' +
                ", senderIP='" + senderIP + '\'' +
                '}';
    }
}
