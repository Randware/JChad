package net.jchad.shared.networking.packets.defaults;

import net.jchad.shared.networking.packets.PacketType;

public class ConnectionEstablishedPacket extends DefaultPacket{
    public ConnectionEstablishedPacket() {
        super(PacketType.CONNECTION_ESTABLISHED, "The connection got successfully established." +
                " You can now send ClientMessagesPackets, LoadChatRequestPacket or ServerInformationRequestPackets");
    }
}
