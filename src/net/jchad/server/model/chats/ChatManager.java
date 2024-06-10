package net.jchad.server.model.chats;

import net.jchad.server.model.config.ConfigObserver;
import net.jchad.server.model.config.store.Config;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.server.Server;
import net.jchad.shared.files.PathWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatManager {
    private static final Path chatsSavePath = Path.of("./chats/");

    private Server server;
    private MessageHandler messageHandler;
    private ConfigObserver configObserver;

    private PathWatcher pathWatcher;
    private int restartAttempts;

    private ConcurrentLinkedDeque<Chat> chats;

    public ChatManager(Server server, ConfigObserver configObserver) {
        this.server = server;
        this.messageHandler = server.getMessageHandler();
        this.configObserver = configObserver;

        ensureChatsSavePathCreated();
        initializePathWatcher();

        try {
            loadChats();
        } catch (IOException e) {
            messageHandler.handleError(new IOException("Failed loading saved chat configuration", e));
            messageHandler.handleWarning("Empty chat configuration was loaded");

            chats = new ConcurrentLinkedDeque<>();
        }
    }

    public ChatManager(Server server) {
        this(server, null);
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

    public ConcurrentLinkedDeque<Chat> getAllChats() {
        return new ConcurrentLinkedDeque<>(chats);
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

        ArrayList<Path> dirs = new ArrayList<>(Files.list(chatsSavePath)
                .filter(Files::isDirectory).toList());

        ConcurrentLinkedDeque<Chat> chats = new ConcurrentLinkedDeque<>();

        for(Path dir : dirs) {
            String chatName = dir.getFileName().toString();

            try {
                chats.add(new Chat(chatName, server));
            } catch (IOException e) {
                messageHandler.handleError(new IOException("Failed loading chat \"" + chatName + "\"", e));
            }

            try {
                pathWatcher.addPath(dir);
            } catch (IOException e) {
                messageHandler.handleError(
                        new IOException("Failed initializing chat config watching for \"" + chatName + "\" chat", e)
                );
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

                    try {
                        modifiedChat.loadConfig();

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
        Config config = server.getConfig();
        int restartMillis = config.getInternalSettings().getPathWatcherRestartCountResetMilliseconds();


        if (System.currentTimeMillis() - pathWatcher.getStartTimestamp() >= restartMillis) {
            restartAttempts = 0;
        }

        restartAttempts++;

        int maxRestarts = config.getInternalSettings().getMaxPathWatcherRestarts();

        if (restartAttempts <= maxRestarts) {
            messageHandler.handleError(new IOException("The PathWatcher thread has run into an unexpected error: " + e));
            messageHandler.handleWarning("Attempting restart " + restartAttempts + " ...");

            if (pathWatcher != null) {
                pathWatcher.stopWatcher();
            }

            initializePathWatcher();
        } else {
            messageHandler.handleError(new IOException("Failed PathWatcher restart after " + maxRestarts + " attempts: " + e));
            messageHandler.handleWarning("Registering live updates in the chat configuration is no longer possible.");

            pathWatcher.stopWatcher();
        }
    }

    public static Path getChatsSavePath() {
        return chatsSavePath;
    }
}
