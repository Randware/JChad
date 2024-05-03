package net.jchad.server.model.chats;

public class ChatConfig {
    private boolean anonymous = false;
    private boolean loadChatHistory = true;
    private long loadChatHistoryMessageCount = 100;

    public ChatConfig() {

    }

    public ChatConfig(boolean anonymous, boolean loadChatHistory, long loadChatHistoryMessageCount) {
        this.anonymous = anonymous;
        this.loadChatHistory = loadChatHistory;
        this.loadChatHistoryMessageCount = loadChatHistoryMessageCount;
    }
}
