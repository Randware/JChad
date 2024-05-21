package net.jchad.client.model.client.packets;

import net.jchad.client.model.client.packets.packetmaps.PacketMapBase;
import net.jchad.client.model.client.packets.packetmaps.ServerMessage;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.messages.ServerMessagePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class maps every package to its respective code that should be executed
 * when this packet is received.
 */
public enum PacketMapper {
    SERVER_MESSAGE(ServerMessagePacket.class, new ServerMessage());

    private static final Map<Class<? extends Packet>, PacketMapBase<? extends Packet>> packetMapRegistry = new HashMap<>();

    static {
        for (PacketMapper mapper : values()) {
            packetMapRegistry.put(mapper.packetType, mapper.packetMap);
        }
    }

    private final Class<? extends Packet> packetType;
    private final PacketMapBase<? extends Packet> packetMap;

    <T extends Packet> PacketMapper(Class<T> packetType, PacketMapBase<T> packetMap) {
        this.packetType = packetType;
        this.packetMap = packetMap;
    }

    public static void executePacket(Packet packet, PacketHandler handler) {
        Optional.ofNullable(packetMapRegistry.get(packet.getClass()))
                .ifPresent(map -> ((PacketMapBase<Packet>) map).executePacket(packet, handler));
    }

    public Class<? extends Packet> getPacketType() {
        return packetType;
    }

    public PacketMapBase<? extends Packet> getPacketMap() {
        return packetMap;
    }
}
