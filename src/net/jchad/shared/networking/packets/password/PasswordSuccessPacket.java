package net.jchad.shared.networking.packets.password;

public class PasswordSuccessPacket extends PasswordPacket{
    public PasswordSuccessPacket() {
        super(PasswordPacketType.PASSWORD_SUCCESS);
    }
}
