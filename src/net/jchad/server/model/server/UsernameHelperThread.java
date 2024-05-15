package net.jchad.server.model.server;

import net.jchad.server.model.users.ConnectionExistsException;
import net.jchad.server.model.users.User;
import net.jchad.server.model.users.UsernameInvalidException;
import net.jchad.server.model.users.UsernameTakenException;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.UsernamePacket;

public class UsernameHelperThread extends HelperThread{
    public UsernameHelperThread(ServerThread serverThread) {
        super(serverThread);
    }

    public User arrangeUser() {
        User user = null;
        for (int fails = 1; fails <= getRetries(); fails++) {
            UsernamePacket usernamePacket = readJSON(UsernamePacket.class, PacketType.USERNAME);
            try {

                user = new User(usernamePacket.getUsername(), getServerThread());
            } catch (NullPointerException | UsernameInvalidException | UsernameTakenException e) {
                int retriesLeft = getRetries() - fails;
                getServerThread().getMessageHandler().handleDebug("A " + e.getClass().getSimpleName() + " occurred during the user arrangement. The connection get terminated "
                        + ((retriesLeft <= 0) ? "now": ("after " + retriesLeft + " more failed attempt(s)")), e);

                if (retriesLeft <= 0) {
                    getServerThread().close("A " + e.getClass().getSimpleName() + " occurred during the user arrangement");
                }
            } catch (ConnectionExistsException e) {
                getServerThread().getMessageHandler().handleDebug("The ServerThread has already been associated with a username. The connection gets terminated now ", e);
                    getServerThread().close("The ServerThread has already been associated with a username");

            }
        }

        //No exception was thrown. Therefore, we can assign the return value
        if (user == null) {
            getServerThread().close("An unknown error occurred during the user arrangement");
            return null; //This should not be returned
        } else {
            return user;
        }
    }
}
