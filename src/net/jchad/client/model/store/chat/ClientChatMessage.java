package net.jchad.client.model.store.chat;

public class ClientChatMessage {
    /**
     * This boolean represents if this specific message was sent by this
     * client, or rather said this message is "this clients own message".
     */
    private boolean own;

    /**
     * The string content of this message.
     */
    private String content;

    public ClientChatMessage(String content) {
        this.content = content;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }
}
