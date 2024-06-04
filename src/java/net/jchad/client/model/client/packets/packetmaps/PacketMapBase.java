package net.jchad.client.model.client.packets.packetmaps;

import net.jchad.client.model.client.connection.ServerConnection;
import net.jchad.client.model.client.connection.ServerConnector;
import net.jchad.client.model.client.packets.PacketHandler;
import net.jchad.shared.networking.packets.Packet;

/**
 * This class is the base for every packet map that should be executed for
 * its respective received packet.
 */
public abstract class PacketMapBase<T extends Packet> {

    /**
     * This method executes the specified {@link Packet} on the specified {@link PacketHandler}.
     * It will first determine if the supplied {@link PacketHandler} is a {@link ServerConnector} or a
     * {@link ServerConnection} and then call the responsible method.
     *
     * @param packet the {@link Packet} that was mapped.
     * @param handler the {@link PacketHandler} responsible for handling this {@link Packet}.
     */
    public final void executePacket(T packet, PacketHandler handler) {
        if (handler instanceof ServerConnector connector) {
            executeConnector(packet, connector);
        } else if (handler instanceof ServerConnection connection) {
            executeConnection(packet, connection);
        }
    }

    /**
     * This abstract method will be executed in case the {@link PacketHandler} is a
     * {@link ServerConnector} instance.
     *
     * @param packet the {@link Packet} that was mapped.
     * @param connector the {@link ServerConnector} responsible for handling this {@link Packet}.
     */
    protected abstract void executeConnector(T packet, ServerConnector connector);

    /**
     * This abstract method will be executed in case the {@link PacketHandler} is a
     * {@link ServerConnection} instance.
     *
     * @param packet the {@link Packet} that was mapped.
     * @param connection the {@link ServerConnection} responsible for handling this {@link Packet}.
     */
    protected abstract void executeConnection(T packet, ServerConnection connection);
}
