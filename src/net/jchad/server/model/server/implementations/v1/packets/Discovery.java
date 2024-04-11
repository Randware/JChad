package net.jchad.server.model.server.implementations.v1.packets;

public enum Discovery implements SubType, PacketString<Discovery>{
    INFO_REQUEST,
    INFO_RESPONSE,
    VERSION_INFO,
    VERSION_AGREEMENT_REQUEST,
    VERSION_AGREEMENT_RESPONSE;

    @Override
    public Discovery stringToValue(String stringEnum) {
        return valueOf(enumName());
    }

    @Override
    public PacketTypes getParent() {
        return PacketTypes.DISCOVERY;
    }

    @Override
    public Object[] getSpecificFields() {
        String type = ""; //INFO/REQUEST/RESPONSE
        Integer version = 1; //CURRENTLY USING networking version
        String information = ""; //
        return new Object[]{type, version, information};
    }
}
