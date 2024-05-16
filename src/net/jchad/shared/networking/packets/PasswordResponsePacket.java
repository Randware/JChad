package net.jchad.shared.networking.packets;

public class PasswordResponsePacket extends PasswordPacket{
    private final String password;
    public PasswordResponsePacket(String password) {
        super(PasswordPacketType.PASSWORD_RESPONSE);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
