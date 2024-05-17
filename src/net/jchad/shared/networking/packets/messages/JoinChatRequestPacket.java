package net.jchad.shared.networking.packets.messages;

public class JoinChatRequestPacket extends JoinChatPacket{

    private final int load_previous_messages;

    public JoinChatRequestPacket(String chat_name, int load_previous_messages) {
        super(JoinChatPacketType.REQUEST, chat_name);
        this.load_previous_messages = load_previous_messages;
    }



    public int getLoad_previous_messages() {
        return load_previous_messages;
    }
}
