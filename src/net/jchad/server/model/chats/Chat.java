package net.jchad.server.model.chats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.jchad.server.model.error.MessageHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * TODO: Implement a way for accessing the messages, which adheres to the "loadChatHistory" and "loadChatHistoryMessageCount" values
 * TODO: Make chat gathering Thread safe
 * DONE: Fix serialization error for IPAddress with GSON
 * - Simply turn IPAddress into a String
 * DONE: Implement config loading and saving
 * DONE: Make the storage location for the ChatConfig retrievable from the outside, necessary for config validation
 */
public class Chat {
    private Gson gson;
    private ObjectMapper mapper;
    private MessageHandler messageHandler;

    private String name;
    private ArrayList<ChatMessage> messages;
    private ChatConfig config;

    private Path savePath;
    private Path messagesSavePath;
    private Path configSavePath;

    public Chat(String name, MessageHandler messageHandler) throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        this.messageHandler = messageHandler;

        this.name = name;
        this.messages = new ArrayList<>();

        savePath = ChatManager.getChatsSavePath().resolve(name);
        messagesSavePath = savePath.resolve("messages.json");
        configSavePath = savePath.resolve("config.yml");

        if (!Files.exists(savePath)) {
            Files.createDirectory(savePath);
        }

        loadConfig();
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
        if (!Files.exists(messagesSavePath)) {
            Files.createFile(messagesSavePath);
        }

        Files.writeString(messagesSavePath, gson.toJson(messages));
    }

    /**
     * This currently loads the chats everytime all chats are reloaded, which is not the expected behaviour.
     * I am also unsure how the "loadChatHistory" and "loadChatHistoryMessageCount" config values should be handled.
     * Should they define how many, or if any chat messages should be stored in this chat?
     * How many should be retrievable from the outside?
     * How many should be loaded on startup? (which does not account for the problematic sending of huge chat histories to the clients)
     */
    private void loadMessages() throws IOException {
        if (!config.isLoadChatHistory()) {
            return;
        }

        if (!Files.exists(messagesSavePath)) {
            saveMessages();
        }

        ArrayList<ChatMessage> loadedMessages = new ArrayList<>();
        try {
            loadedMessages = gson.fromJson(Files.readString(messagesSavePath), new TypeToken<ArrayList<ChatMessage>>(){}.getType());
        } catch (JsonSyntaxException e) {
            messageHandler.handleError(new Exception("Failed loading messages from \"messages.json\" file, it seems to be malformed"));
        }

        Collections.reverse(loadedMessages);

        this.messages = loadedMessages;
    }

    public void saveConfig() throws IOException {
        if (!Files.exists(configSavePath)) {
            Files.createFile(configSavePath);
        }

        mapper.writeValue(configSavePath.toFile(), new ChatConfig());
    }

    public void loadConfig() throws IOException {
        if (!Files.exists(configSavePath)) {
            saveConfig();
        }

        try {
            this.config = mapper.readValue(configSavePath.toFile(), ChatConfig.class);
        } catch (Exception e) {
            switch (e) {
                case InvalidFormatException ife -> {
                    messageHandler.handleWarning("\"" + name + "\" chat configuration couldn't be parsed: "
                            + ife.getOriginalMessage());
                }

                case MarkedYAMLException mye -> {
                    messageHandler.handleWarning("\"" + name + "\" chat configuration couldn't be parsed: "
                            + mye.getProblem() + "\n"
                            + mye.getContextMark().get_snippet(0, 150));
                }

                case MismatchedInputException mie -> {
                    messageHandler.handleWarning("\"" + name + "\" chat configuration couldn't be parsed: "
                            + mie.getOriginalMessage());
                }

                default -> {
                    messageHandler.handleError(e);
                }
            }

            if (this.config == null) {
                messageHandler.handleWarning("Falling back to default chat configuration");
                this.config = new ChatConfig();
            } else {
                messageHandler.handleWarning("Continuing with last working chat configuration");
            }
        }
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

    public Path getConfigSavePath() {
        return configSavePath;
    }

    public Path getMessagesSavePath() {
        return messagesSavePath;
    }

    @Override
    public String toString() {
        return "Chat{" +
                ", name='" + name + '\'' +
                ", messages=" + messages +
                ", config=" + config +
                ", savePath=" + savePath +
                ", messagesSavePath=" + messagesSavePath +
                '}';
    }
}
