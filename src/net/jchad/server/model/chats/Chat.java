package net.jchad.server.model.chats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.jchad.shared.networking.ip.IPAddress;
import net.jchad.shared.networking.ip.IPv4Address;
import net.jchad.shared.networking.ip.InvalidIPAddressException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * DONE: Fix serialization error for IPAddress with GSON
 *       - Simply turn IPAddress into a String
 * TODO: Implement config loading and saving
 * TODO: Make the storage location for the ChatConfig retrievable from the outside, necessary for config validation
 */
public class Chat {
    private Gson gson;

    private String name;
    private ArrayList<ChatMessage> messages;
    private ChatConfig config;

    private Path savePath;
    private Path messagesSavePath;

    public Chat(String name) throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.name = name;
        this.messages = new ArrayList<>();
        messages.add(new ChatMessage(1, System.currentTimeMillis(), "Darius", "192.0.0.1"));
        messages.add(new ChatMessage(2, System.currentTimeMillis(), "Lukas", "192.0.0.2"));

        savePath = ChatManager.getChatsSavePath().resolve(name);
        messagesSavePath = savePath.resolve("messages.json");

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
        if(!Files.exists(savePath)) {
            Files.createDirectory(savePath);
        }

        Files.writeString(Files.createFile(messagesSavePath), gson.toJson(messages));
    }

    private void loadMessages() throws IOException {
        // Load saved messages from file here
        // If not messages are saved in this path yet, create all files first.
        if(!Files.exists(messagesSavePath)) {
            saveMessages();
        }

        this.messages = gson.fromJson(Files.readString(messagesSavePath), new TypeToken<ArrayList<ChatMessage>>(){}.getType());
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
