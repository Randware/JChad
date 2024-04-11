package net.jchad.server.model.server.implementations.v1.packets;

public enum Room implements SubType, PacketString<Room>{
    LIST_ALL,
    LIST_ACCESSIBLE,
    LIST_PROTECTED,
    ENTER,
    PASSWORD_REQUEST,
    PASSWORD_RESPONSE;
    @Override
    public Room stringToValue(String stringEnum) {
        return valueOf(enumName());
    }

    @Override
    public PacketTypes getParent() {
        return PacketTypes.ROOM;
    }

    @Override
    public Object[] getSpecificFields() {
        return new Object[0];
    }
}
