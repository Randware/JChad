package net.jchad.server.model.chats;

import net.jchad.shared.networking.packets.messages.ServerMessagePacket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ChatMessage {
    private int id;
    private String content;
    private long timestamp;
    private String senderName;

    private String senderIP;

    public ChatMessage(String content, long timestamp, String senderName, String senderIP) {
        this.content = content;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public LocalDateTime getTimestampAsLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", senderName='" + senderName + '\'' +
                ", senderIP='" + senderIP + '\'' +
                '}';
    }

    /**
     * Creates a {@link ChatMessage} from a {@link ServerMessagePacket}.
     *
     * @param messagePacket The packet that get converted to a {@link ChatMessage}
     * @return a {@link ChatMessage} with the values from the {@link ServerMessagePacket}
     */
    public static ChatMessage fromMessagePacket(ServerMessagePacket messagePacket, String senderIP) {
        return new ChatMessage(messagePacket.getChat(), messagePacket.getTimestamp(), messagePacket.getUsername(), senderIP);
    }
}
