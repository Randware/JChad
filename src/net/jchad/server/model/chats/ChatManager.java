package net.jchad.server.model.chats;

import net.jchad.server.model.config.ConfigObserver;
import net.jchad.server.model.config.store.ConfigFile;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.shared.files.PathWatcher;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
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

        ensureChatsSavePathCreated();
        initializePathWatcher();

        try {
            loadChats();
        } catch (IOException e) {
            messageHandler.handleError(new IOException("Failed loading saved chat configuration", e));
            messageHandler.handleWarning("Empty chat configuration was loaded");

            chats = new ArrayList<>();
        }
    }

    public ChatManager(MessageHandler messageHandler) {
        this(messageHandler, null);
    }

    private void initializePathWatcher() {
        try {
            pathWatcher = new PathWatcher(chatsSavePath, this::configUpdated, this::handlePathWatcherError);
            pathWatcher.start();
        } catch (IOException e) {
            handlePathWatcherError(new IOException("Failed initializing chat config watching", e));
        }
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

    private void ensureChatsSavePathCreated() {
        if(!Files.exists(chatsSavePath)) {
            try {
                Files.createDirectory(chatsSavePath);
            } catch (IOException e) {
                messageHandler.handleFatalError(new IOException("The \"chats\" directory couldn't be created for chat saving"));
            }
        }

    }

    private void loadChats() throws IOException {
        ensureChatsSavePathCreated();

        ArrayList<Path> dirs = new ArrayList<>(Files.list(chatsSavePath).filter(Files::isDirectory).toList());

        ArrayList<Chat> chats = new ArrayList<>();

        for(Path dir : dirs) {
            String chatName = dir.getFileName().toString();

            try {
                chats.add(new Chat(chatName));
            } catch (IOException e) {
                messageHandler.handleError(new IOException("Failed loading chat \"" + chatName + "\"", e));
            }

            try {
                pathWatcher.addPath(dir);
            } catch (IOException e) {
                messageHandler.handleError(new IOException("Failed initializing chat config watching for \"" + chatName + "\" chat", e));
                messageHandler.handleWarning("Live updating the \"" + chatName + "\" chat configuration is not possible");
            }
        }

        this.chats = chats;
    }

    private void configUpdated(Path path, WatchEvent.Kind<?> kind) {
        if(kind == StandardWatchEventKinds.ENTRY_CREATE) {
            if(Files.isDirectory(path)) {
                messageHandler.handleInfo("\"" + path.getFileName().toString() + "\" chat was created");

                try {
                    loadChats();

                    if(configObserver != null) {
                        configObserver.configUpdated();
                    }
                } catch (IOException e) {
                    messageHandler.handleError(new IOException("Failed loading \"" + path.getFileName().toString() + "\" chat"));
                    messageHandler.handleWarning("Continuing with old chat configuration");
                }
            }
        } else if(kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            // TODO: Not call this method when there is a file modified in this entry
            if(Files.isDirectory(path)) {
                messageHandler.handleInfo("Chat configuration was updated");

                try {
                    loadChats();

                    if(configObserver != null) {
                        configObserver.configUpdated();
                    }
                } catch (IOException e) {
                    messageHandler.handleError(new IOException("Failed loading new chat configuration", e));
                    messageHandler.handleWarning("Continuing with old chat configuration");
                }
            } else {
                String modifiedChatName = path.getParent().getFileName().toString();

                Chat modifiedChat = getChat(modifiedChatName);

                if(modifiedChat == null) return;

                Path chatConfigPath = modifiedChat.getConfigSavePath().toAbsolutePath().normalize();

                if(chatConfigPath.equals(path)) {
                    messageHandler.handleInfo("\"" + modifiedChatName + "\" chat configuration was updated");

                    // TODO: Load chat configuration here instead of reloading all chats
                    try {
                        loadChats();

                        if(configObserver != null) {
                            configObserver.configUpdated();
                        }
                    } catch (IOException e) {
                        messageHandler.handleError(new IOException("Failed loading new chat configuration", e));
                        messageHandler.handleWarning("Continuing with old chat configuration");
                    }
                }
            }
        }
    }

    private void handlePathWatcherError(Exception e) {
        // Handle error, restart, etc.
    }

    public static Path getChatsSavePath() {
        return chatsSavePath;
    }
}
