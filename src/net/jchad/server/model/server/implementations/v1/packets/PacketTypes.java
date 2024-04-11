package net.jchad.server.model.server.implementations.v1.packets;

public enum PacketTypes implements PacketString<PacketTypes> {
    DISCOVERY,
    ENCRYPT,
    AUTHENTICATE,
    ROOM,
    MESSAGE;


    @Override
    public PacketTypes stringToValue(String stringEnum) {
        return valueOf(enumName());
    }
}
