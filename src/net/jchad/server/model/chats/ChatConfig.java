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

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isLoadChatHistory() {
        return loadChatHistory;
    }

    public void setLoadChatHistory(boolean loadChatHistory) {
        this.loadChatHistory = loadChatHistory;
    }

    public long getLoadChatHistoryMessageCount() {
        return loadChatHistoryMessageCount;
    }

    public void setLoadChatHistoryMessageCount(long loadChatHistoryMessageCount) {
        this.loadChatHistoryMessageCount = loadChatHistoryMessageCount;
    }

    @Override
    public String toString() {
        return "ChatConfig{" +
                "anonymous=" + anonymous +
                ", loadChatHistory=" + loadChatHistory +
                ", loadChatHistoryMessageCount=" + loadChatHistoryMessageCount +
                '}';
    }
}
