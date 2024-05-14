package net.jchad.shared.networking.packets;

import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.server.Server;

public class ServerInformationPacket implements Packet{
    private final PacketType packet_type = PacketType.SERVER_INFORMATION;

    private final Double server_version;
    private final Boolean encrypt_communications;
    private final Boolean encrypt_messages;
    private final String[] available_chats;
    private final Boolean requires_password;
    private final Boolean whitelist;
    private final Boolean strictly_anonymous;

    protected ServerInformationPacket(Double server_version,Boolean encrypt_communications, Boolean encrypt_messages, String[] available_chats, Boolean requires_password, Boolean whitelist, Boolean strictly_anonymous) {
        this.server_version = server_version;
        this.encrypt_communications = encrypt_communications;
        this.encrypt_messages = encrypt_messages;
        this.available_chats = available_chats;
        this.requires_password = requires_password;
        this.whitelist = whitelist;
        this.strictly_anonymous = strictly_anonymous;
    }

    public static ServerInformationPacket getCurrentServerInfo(Server server) {
        return new ServerInformationPacket(server.getVersion(),
                server.getConfig().getServerSettings().isEncryptCommunications(),
                     server.getConfig().getServerSettings().isEncryptMessages(),
                        server.getChats().stream().map(Chat::getName).toArray(String[]::new),
                                      server.getConfig().getServerSettings().isRequiresPassword(),
                                      server.getConfig().getServerSettings().isWhitelist(),
                                      server.getConfig().getServerSettings().isStrictlyAnonymous());
    }
}
