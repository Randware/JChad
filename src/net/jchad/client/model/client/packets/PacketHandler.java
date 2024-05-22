package net.jchad.client.model.client.packets;

import net.jchad.shared.networking.packets.Packet;

public interface PacketHandler {
    void disposePacket(Packet packet);

    void handlePacketString(String packetString);

    void handlePacketReaderError(Exception e);
}
