package net.jchad.server.model.server.util;

import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.*;
import net.jchad.shared.networking.packets.password.PasswordFailedPacket;
import net.jchad.shared.networking.packets.password.PasswordRequestPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.password.PasswordSuccessPacket;

public class PasswordHelperThread extends HelperThread {

    private final int passwordAttempts;

    public PasswordHelperThread(ServerThread serverThread) {
        super(serverThread);
        this.passwordAttempts = serverThread.getServer().getConfig().getInternalSettings().getPasswordAttempts();
    }

    /**
     * Gets the password from the client.
     * If the client fails to provide the correct password, the connection gets closed.
     */
    public void getPassword() {
        getServerThread().getMessageHandler().handleDebug("%s started the PasswordHelperThread".formatted(getServerThread().getRemoteAddress()));
        writePacket(new PasswordRequestPacket());
        for (int failedAttempts = 0; failedAttempts < passwordAttempts; failedAttempts++) {
            try {
                PasswordResponsePacket passwordPacket = readJSON(PasswordResponsePacket.class, PacketType.PASSWORD);

                if (passwordPacket.getPassword().length() != 44) {
                    //You may wonder: Why 44?
                    // Simple answer: Every base64 encoded sha256 hash has to be 44 Base64 chars long
                    writePacket(new PasswordFailedPacket("The provided password is not SHA-256 hashed and base64 encoded"));
                    throw new InvalidPacketException("The provided password from %s was not SHA-256 hashed and Base64 encoded".formatted(getServerThread().getRemoteAddress()));
                }
                if (!passwordPacket.getPassword().equals(getServerThread().getServer().getServerPassword())) {
                    writePacket(new PasswordFailedPacket("The provided password did not match the server's password."));
                    throw new InvalidPacketException("The provided password from %s did not match the server's password".formatted(getServerThread().getRemoteAddress()));
                } else {
                    getServerThread().getMessageHandler().handleDebug("%s authenticated successfully ".formatted(getServerThread().getRemoteAddress()));
                    writePacket(new PasswordSuccessPacket());
                    break; //Client sent correct password
                }
            } catch (InvalidPacketException e) {

                if (failedAttempts+1 >= passwordAttempts) {
                    getServerThread().getMessageHandler().handleDebug(e.getMessage() + ". The connection gets terminated now.");
                    getServerThread().close(e.getMessage());
                }

                getServerThread().getMessageHandler().handleDebug("%s . The connection closes after %d more failed attempt(s)".formatted(e.getMessage(), passwordAttempts-failedAttempts-1));
            }
        }
    }
}
