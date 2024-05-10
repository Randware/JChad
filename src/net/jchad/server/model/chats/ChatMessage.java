package net.jchad.server.model.chats;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ChatMessage {
    private long id;
    private String content;
    private boolean encrypted;
    private long timestamp;
    private String senderName;
    private String senderIP;

    public ChatMessage(long id, String content, boolean encrypted, long timestamp, String senderName, String senderIP) {
        this.id = id;
        this.content = content;
        this.encrypted = encrypted;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderIP = senderIP;
    }

    public ChatMessage(long id, String content, boolean encrypted, long timestamp) {
        this(id, content, encrypted, timestamp, null, null);
    }

    public long getId() {
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
