package net.jchad.shared.networking.packets.password;

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
