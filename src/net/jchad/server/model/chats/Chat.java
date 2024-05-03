package net.jchad.server.model.chats;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class Chat {
    private String name;
    private ArrayList<ChatMessage> messages;
    private ChatConfig config;

    private Path savePath;

    public Chat(String name) throws IOException {
        this.name = name;

        savePath = ChatManager.getChatsSavePath().resolve(name);

        loadMessages();
    }
    
    /*
    Add message, then try to save modified messages. If it fails throw Exception.
    If an exception is thrown, it means, the message was added, but couldn't be written to file.
     */
    public void addMessage(ChatMessage message) throws IOException {
        messages.add(message);

        saveMessages();
    }

    private void saveMessages() throws IOException {
        // Save messages to file here
    }

    private void loadMessages() throws IOException {
        // Load saved messages from file here
        // If not messages are saved in this path yet, create all files first.
    }

    public String getName() {
        return name;
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }

    public ChatConfig getConfig() {
        return config;
    }

    public Path getSavePath() {
        return savePath;
    }
}
