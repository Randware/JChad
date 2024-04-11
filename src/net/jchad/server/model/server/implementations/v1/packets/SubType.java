package net.jchad.server.model.server.implementations.v1.packets;

public interface SubType {
    PacketTypes getParent();

    Object[] getSpecificFields();
}
