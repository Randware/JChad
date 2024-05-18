package net.jchad.shared.networking.packets.messages;

public class LoadChatRequestPacket extends LoadChatPacket {



    public LoadChatRequestPacket(String chat_name) {
        super(JoinChatPacketType.REQUEST, chat_name);

    }




}
