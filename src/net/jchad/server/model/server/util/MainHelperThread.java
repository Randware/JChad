package net.jchad.server.model.server.util;

import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.PacketType;

/**
 * This is the main helper thread that gets used when everything is initialized
 */
public class MainHelperThread extends HelperThread {

    private final int fails = 1;
    public MainHelperThread(ServerThread serverThread) {
        super(serverThread);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            ClientMessagePacket clientMessagePacket = readJSON(ClientMessagePacket.class, PacketType.CLIENT_MESSAGE);
            getServerThread().getUser().sendMessage(clientMessagePacket);
        }
    }
}
