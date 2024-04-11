package net.jchad.server.model.server.implementations.v1.packets;

public enum Authenticate implements SubType,PacketString<Authenticate>{
    PASSWORD_STATUS,
    PASSWORD,
    USERNAME;


    @Override
    public Authenticate stringToValue(String stringEnum) {
        return valueOf(enumName());
    }

    @Override
    public PacketTypes getParent() {
        return PacketTypes.AUTHENTICATE;
    }

    @Override
    public Object[] getSpecificFields() {
        String type = ""; //REQUEST/STATUS/RESPONSE
        String information = ""; //USERNAME/PASSWORD
        Boolean success = false; //TRUE/FALSE
        return new Object[]{type, information, success};
    }
}
