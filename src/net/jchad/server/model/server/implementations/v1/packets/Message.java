package net.jchad.server.model.server.implementations.v1.packets;

public enum Message implements SubType, PacketString<Message>{
    SEND,
    RECEIVE,
    USER_HISTORY,
    WHOLE_HISTORY,
    COMMAND;

    @Override
    public Message stringToValue(String stringEnum) {
        return valueOf(enumName());
    }

    @Override
    public PacketTypes getParent() {
        return PacketTypes.MESSAGE;
    }

    @Override
    public Object[] getSpecificFields() {
        String type = ""; //SEND/RECEIVE/COMMAND/HISTORY
        String user = ""; //USERNAME OR COMMAND SUFFIX
        String message = ""; //MESSAGE OR COMMAND
        Boolean history_full = false; //true --> USER_HISTORY
        String[][] history = {{user,message}};
        return new Object[]{type, user,message, history_full, history};
    }
}
