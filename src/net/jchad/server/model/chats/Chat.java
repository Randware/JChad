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
import net.jchad.server.model.server.Server;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.messages.ServerMessagePacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
    private Server server;
    private MessageHandler messageHandler;

    private String name;
    private ArrayList<ChatMessage> messages;
    private ChatConfig config;

    private Path savePath;
    private Path messagesSavePath;
    private Path configSavePath;

    public Chat(String name, Server server) throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        this.server = server;
        this.messageHandler = server.getMessageHandler();

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

    Use synchronized here to ensure the messages are added in the right order.
     */
    public synchronized void addMessage(ChatMessage message) throws IOException {
        if(!config.isSaveMessages()) {
            return;
        }

        // This anonymises the message if anonymous is enabled
        if(config.isAnonymous() || server.getConfig().getServerSettings().isStrictlyAnonymous()) {
            message.setSenderName(server.getConfig().getInternalSettings().getAnonymousUserName());
            message.setSenderIP(server.getConfig().getInternalSettings().getAnonymousUserIP());
        }


        messages.add(message.appendID(messages.size()));

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
            loadedMessages = gson.fromJson(Files.readString(messagesSavePath), new TypeToken<ArrayList<ChatMessage>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            messageHandler.handleError(new Exception("Failed loading messages from \"messages.json\" file, it seems to be malformed"));
        }

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

    /**
     * <font color="red">This method should ideally not be called, since it does not
     * respect the configured settings for the message loading of this chat.</font>
     *
     * It is sorted from oldest to newest. That means the first index is the oldest message
     * and the last index is the newest message.
     *
     * @return all messages from this chat
     */
    public ArrayList<ChatMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }

    /**
     * <font color="red">This method should be called when accessing messages in this chat,
     * as it respects the configured message loading values.</font>
     *
     * It is sorted from oldest to newest. That means the first index is the oldest message
     * and the last index is the newest message.
     *
     * @return all messages from this chat according to the configuration of this chat
     */
    public ArrayList<ChatMessage> getMessages() {
        if (!config.isLoadChatHistory()) return new ArrayList<>();

        ArrayList<ChatMessage> selectedMessages = new ArrayList<>();

        int messageCount = (int) Math.min(config.getLoadChatHistoryMessageCount(), messages.size());

        for (int x = messages.size() - messageCount; x < messages.size(); x++) {
            selectedMessages.add(messages.get(x));
        }

        return selectedMessages;
    }

    /**
     * <font color="red">This method should be called when accessing messages in this chat,
     * as it respects the configured message loading values.</font>
     *
     * It is sorted from oldest to newest. That means the first index is the oldest message
     * and the last index is the newest message.
     *
     *
     * @return all messages from this chat according to the configuration of this chat converted into {@link ServerMessagePacket}
     */
    public List<ServerMessagePacket> getServerMessages(CrypterManager crypterManager, MessageHandler messageHandler) {

        if (!config.isLoadChatHistory()) return new ArrayList<>();

        ArrayList<ServerMessagePacket> selectedMessages = new ArrayList<>();

        int messageCount = (int) Math.min(config.getLoadChatHistoryMessageCount(), messages.size());

        if (crypterManager == null) {
            for (int x = messages.size() - messageCount; x < messages.size(); x++) {
                selectedMessages.add(messages.get(x).toMessagePacket(name));
            }
        } else {
            //If chat message content needs to get encrypted before returning
            for (int x = messages.size() - messageCount; x < messages.size(); x++) {
                ServerMessagePacket messagePacket = messages.get(x).toMessagePacket(name);
                try {
                    System.out.println(new ServerMessagePacket(
                            crypterManager.decryptAES(messagePacket.getMessage()),
                            messagePacket.getChat(),
                            messagePacket.getUsername(),
                            messagePacket.getTimestamp()).toJSON());
                    selectedMessages.add(new ServerMessagePacket(
                            crypterManager.decryptAES(messagePacket.getMessage()),
                            messagePacket.getChat(),
                            messagePacket.getUsername(),
                            messagePacket.getTimestamp()
                    ));
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         NoSuchAlgorithmException | BadPaddingException | InvalidKeyException |
                         ImpossibleConversionException e) {
                    messageHandler.handleDebug("An error occurred while encrypting the content of a saved chat massage in " + name + " with the ID " + messages.get(x).getId());
                }
            }
        }
        return selectedMessages;
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
