package net.jchad.shared.networking.packets.messages;

public class JoinChatRequestPacket extends JoinChatPacket {



    public JoinChatRequestPacket(String chat_name) {
        super(JoinChatPacketType.REQUEST, chat_name);

    }




}
