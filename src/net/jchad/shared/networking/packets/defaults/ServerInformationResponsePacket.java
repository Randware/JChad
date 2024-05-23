package net.jchad.shared.networking.packets.defaults;

import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.server.Server;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.PacketType;

import java.util.Arrays;

public class ServerInformationResponsePacket implements Packet {
    private final PacketType packet_type = PacketType.SERVER_INFORMATION_RESPONSE;

    private final double server_version;
    private final boolean encrypt_communications;
    private final boolean encrypt_messages;
    private final String[] available_chats;
    private final boolean requires_password;
    private final boolean strictly_anonymous;
    private final String username_validation_regex;
    private final String username_validation_description;

    protected ServerInformationResponsePacket(double server_version, boolean encrypt_communications, boolean encrypt_messages, String[] available_chats, boolean requires_password, boolean strictly_anonymous, String username_validation_regex, String username_validation_description) {
        this.server_version = server_version;
        this.encrypt_communications = encrypt_communications;
        this.encrypt_messages = encrypt_messages;
        this.available_chats = available_chats;
        this.requires_password = requires_password;
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
                ", strictly_anonymous=" + strictly_anonymous +
                ", username_validation_regex='" + username_validation_regex + '\'' +
                ", username_validation_description='" + username_validation_description + '\'' +
                '}';
    }

    public static ServerInformationResponsePacket getCurrentServerInfo(Server server) {
        return new ServerInformationResponsePacket(server.getVersion(),
                server.getConfig().getServerSettings().isEncryptCommunications(),
                     server.getConfig().getServerSettings().isEncryptMessages(),
                        server.getChats().stream().map(Chat::getName).toArray(String[]::new),
                     server.getConfig().getServerSettings().isRequiresPassword(),
                                      server.getConfig().getServerSettings().isWhitelist(),
               server.getConfig().getInternalSettings().getUsernameRegex(),
           server.getConfig().getInternalSettings().getUsernameRegexDescription());
    }
}
