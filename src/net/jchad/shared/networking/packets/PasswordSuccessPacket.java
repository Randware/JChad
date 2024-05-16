package net.jchad.shared.networking.packets;

public class PasswordSuccessPacket extends PasswordPacket{
    public PasswordSuccessPacket() {
        super(PasswordPacketType.PASSWORD_SUCCESS);
    }
}
