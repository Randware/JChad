package net.jchad.client.model.store.chat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

// TODO: Figure out how to differentiate the chat messages that are received by the server and which are sent by the client
//          (See Client class sendMessageString() method)
public class ClientChatMessage {
    /**
     * This boolean represents if this specific message was sent by this
     * client, or rather said this message is "this clients own message".
     */
    private boolean own;

    /**
     * The chat this message was sent in.
     */
    private String chat;

    /**
     * The string content of this message.
     */
    private String content;

    /**
     * The username of the user this message was sent from.
     */
    private String username;

    /**
     * The epoch timestamp of when this chat message was received by the server.
     */
    private long timestamp;


    public ClientChatMessage(String chat, String content, String username, long timestamp) {
        this.chat = chat;
        this.content = content;
        this.username = username;
        this.timestamp = timestamp;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public String getChat() {
        return chat;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LocalDateTime getPrettyTimestamp() {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
