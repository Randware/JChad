package net.jchad.server.model.chats;

import net.jchad.server.model.config.ConfigObserver;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.shared.files.PathWatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;

public class ChatManager {
    private static final Path chatsSavePath = Path.of("./chats/");

    private MessageHandler messageHandler;
    private ConfigObserver configObserver;
    private PathWatcher pathWatcher;

    private ArrayList<Chat> chats;

    public ChatManager(MessageHandler messageHandler, ConfigObserver configObserver) {
        this.messageHandler = messageHandler;
        this.configObserver = configObserver;

        pathWatcher = new PathWatcher(chatsSavePath, this::configUpdated, this::handlePathWatcherError);
    }

    public ChatManager(MessageHandler messageHandler) {
        this(messageHandler, null);
    }

    public boolean chatExists(String name) {
        return chats.stream().anyMatch(chat -> chat.getName().equals(name));
    }

    public Chat getChat(String name) {
        for (Chat chat : chats) {
            if (chat.getName().equals(name)) {
                return chat;
            }
        }

        return null;
    }

    public ArrayList<Chat> getAllChats() {
        return chats;
    }

    private void loadChats() {
        // Load all chats from chatsSavePath directory
        /*
        Loop through directory, for each library create new Chat with directory name.
        The constructor of the Chat will create all files and load them accordingly.
        If the Chat throws an exception during the creation/loading process, skip the chat.
         */
    }

    private void configUpdated(WatchEvent<?> event) {
        // Handle logging, notify configObserver etc.
    }

    private void handlePathWatcherError(Exception e) {
        // Handle error, restart, etc.
    }

    public static Path getChatsSavePath() {
        return chatsSavePath;
    }
}
