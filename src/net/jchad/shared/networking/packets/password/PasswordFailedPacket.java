package net.jchad.shared.networking.packets.password;

public class PasswordFailedPacket extends PasswordPacket{
    private final String error_message;

    public PasswordFailedPacket(String error_message) {
        super(PasswordPacketType.PASSWORD_FAILED);
        this.error_message = error_message;
    }
}
