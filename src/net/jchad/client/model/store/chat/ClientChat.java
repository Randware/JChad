package net.jchad.client.model.store.chat;

import java.util.ArrayList;

public class ClientChat {
    /**
     * Maybe use a {@link java.util.concurrent.ConcurrentLinkedQueue} or {@link java.util.concurrent.ConcurrentLinkedDeque}
     * in the future if thread-safety or concurrency is a concern in the future.
     *
     * Probably use the ConcurrentLinkedQueue, as it maintains order (which is important in
     * this context).
     */
    private ArrayList<ClientChatMessage> messages;

    public ArrayList<ClientChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }
}
