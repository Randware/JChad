package net.jchad.shared.networking.packets;

public class UsernameErrorPacket implements Packet{
    private final PacketType packet_type = PacketType.USERNAME_ERROR;
    private final UsernameErrorType username_error_type;
    private final String username_error_reason;

    public UsernameErrorPacket(UsernameErrorType username_error_type, String username_error_reason) {
        this.username_error_type = username_error_type;
        this.username_error_reason = username_error_reason;
    }

    public enum UsernameErrorType{
        USERNAME_TAKEN,
        USERNAME_INAPROPRIATE,
        USERNAME_INVALID;
    }
}
