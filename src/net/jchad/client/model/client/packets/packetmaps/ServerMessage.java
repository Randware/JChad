package net.jchad.client.model.client.packets.packetmaps;

import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;
import net.jchad.shared.networking.packets.messages.ServerMessagePacket;

public class ServerMessage extends PacketMapBase<ServerMessagePacket> {

    @Override
    protected void executeConnector(ServerMessagePacket packet, ServerConnector connector) {

    }

    @Override
    protected void executeConnection(ServerMessagePacket packet, ServerConnection connection) {
        // Implement logic for ServerConnection if needed

    }
}
