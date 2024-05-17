package net.jchad.shared.networking.packets.username;

public class UsernameServerPacket extends UsernamePacket {
    private final UsernameResponseType username_response_type;
    private final String username_response_reason;

    public UsernameServerPacket(UsernameResponseType username_response_type, String username_response_reason) {
        this.username_response_type = username_response_type;
        this.username_response_reason = username_response_reason;
    }

    public enum UsernameResponseType {
        ERROR_USERNAME_TAKEN,
        ERROR_USERNAME_INAPROPRIATE,
        ERROR_USERNAME_INVALID,
        SUCCESS_USERNAME_SET,
        PROVIDE_USERNAME;
    }
}
