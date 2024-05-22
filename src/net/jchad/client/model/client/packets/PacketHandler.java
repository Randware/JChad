package net.jchad.client.model.client.packets;

import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;

public sealed interface PacketHandler permits ServerConnector, ServerConnection {
    void handlePacketString(String string);

    void handlePacketReaderError(Exception e);
}
