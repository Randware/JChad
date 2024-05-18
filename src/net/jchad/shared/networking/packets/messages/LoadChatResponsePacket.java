package net.jchad.shared.networking.packets.messages;

import net.jchad.server.model.chats.ChatMessage;

import java.util.List;

public class LoadChatResponsePacket extends LoadChatPacket {

    public final List<ChatMessage> previous_messages;
    
    public LoadChatResponsePacket(String chat_name, List<ChatMessage> previous_messages) {
        super(JoinChatPacketType.RESPONSE, chat_name);
        this.previous_messages = previous_messages;
    }
}
