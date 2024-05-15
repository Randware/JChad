package net.jchad.shared.networking.packets;

import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.server.Server;

import java.util.Arrays;

public class ServerInformationPacket implements Packet{
    private final PacketType packet_type = PacketType.SERVER_INFORMATION;

    private final Double server_version;
    private final Boolean encrypt_communications;
    private final Boolean encrypt_messages;
    private final String[] available_chats;
    private final Boolean requires_password;
    private final Boolean whitelist;
    private final Boolean strictly_anonymous;
    private final String username_validation_regex;
    private final String username_validation_description;

    protected ServerInformationPacket(Double server_version,Boolean encrypt_communications, Boolean encrypt_messages, String[] available_chats, Boolean requires_password, Boolean whitelist, Boolean strictly_anonymous, String username_validation_regex, String username_validation_description) {
        this.server_version = server_version;
        this.encrypt_communications = encrypt_communications;
        this.encrypt_messages = encrypt_messages;
        this.available_chats = available_chats;
        this.requires_password = requires_password;
        this.whitelist = whitelist;
        this.strictly_anonymous = strictly_anonymous;
        this.username_validation_regex = username_validation_regex;
        this.username_validation_description = username_validation_description;
    }

    @Override
    public String toString() {
        return "ServerInformationPacket{" +
                "packet_type=" + packet_type +
                ", server_version=" + server_version +
                ", encrypt_communications=" + encrypt_communications +
                ", encrypt_messages=" + encrypt_messages +
                ", available_chats=" + Arrays.toString(available_chats) +
                ", requires_password=" + requires_password +
                ", whitelist=" + whitelist +
                ", strictly_anonymous=" + strictly_anonymous +
                ", username_validation_regex='" + username_validation_regex + '\'' +
                ", username_validation_description='" + username_validation_description + '\'' +
                '}';
    }

    public static ServerInformationPacket getCurrentServerInfo(Server server) {
        return new ServerInformationPacket(server.getVersion(),
                server.getConfig().getServerSettings().isEncryptCommunications(),
                     server.getConfig().getServerSettings().isEncryptMessages(),
                        server.getChats().stream().map(Chat::getName).toArray(String[]::new),
                     server.getConfig().getServerSettings().isRequiresPassword(),
                                      server.getConfig().getServerSettings().isWhitelist(),
                     server.getConfig().getServerSettings().isStrictlyAnonymous(),
               server.getConfig().getInternalSettings().getUsernameRegex(),
           server.getConfig().getInternalSettings().getUsernameRegexDescription());
    }
}