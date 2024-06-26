package net.jchad.server.model.server.util;

import net.jchad.server.model.server.Server;
import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.defaults.ServerInformationResponsePacket;

public class ServerInformationHelperThread extends HelperThread{
    public ServerInformationHelperThread(ServerThread serverThread) {
        super(serverThread);
    }


    public void sendInformation() {
        writePacket(Server.getCurrentServerInfo(getServerThread().getServer()));
    }
}
