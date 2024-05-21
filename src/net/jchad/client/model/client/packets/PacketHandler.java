package net.jchad.client.model.client.packets;

import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;
import net.jchad.shared.networking.packets.Packet;

public sealed interface PacketHandler permits ServerConnector, ServerConnection {
    void disposePacket(Packet packet);

    void handlePacketString(String string);

    void handlePacketReaderError(Exception e);
}
