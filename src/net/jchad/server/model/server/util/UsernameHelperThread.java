package net.jchad.server.model.server.util;

import net.jchad.server.model.server.ServerThread;
import net.jchad.server.model.users.ConnectionExistsException;
import net.jchad.server.model.users.User;
import net.jchad.server.model.users.UsernameInvalidException;
import net.jchad.server.model.users.UsernameTakenException;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.username.UsernameResponsePacket;
import net.jchad.shared.networking.packets.username.UsernamePacket;

public class UsernameHelperThread extends HelperThread {
    private final String usernameRegexDescription;

    public UsernameHelperThread(ServerThread serverThread) {
        super(serverThread);
        usernameRegexDescription = serverThread.getConfig().getInternalSettings().getUsernameRegexDescription();
    }

    public User arrangeUser() {
        User user = null;
        for (int fails = 1; fails <= getRetries(); fails++) {
            UsernamePacket usernamePacket = readJSON(UsernamePacket.class, PacketType.USERNAME);
            try {

                user = new User(usernamePacket.getUsername(), getServerThread());
                getServerThread().getPrintWriter().println(new UsernameResponsePacket(UsernameResponsePacket.UsernameResponseType.SUCCESS_USERNAME_SET, "The username was successfully set").toJSON());
                getServerThread().getPrintWriter().flush();
                    return user;

            } catch (NullPointerException e) {
                int retriesLeft = getRetries() - fails;
                getServerThread().getMessageHandler().handleDebug("A NullPointerException occurred during the user arrangement. The connection get terminated "
                        + ((retriesLeft <= 0) ? "now": ("after " + retriesLeft + " more failed attempt(s)")), e);

                if (retriesLeft <= 0) {
                    getServerThread().close("A NullPointerException occurred during the user arrangement");
                }
            } catch (ConnectionExistsException e) {
                getServerThread().getMessageHandler().handleDebug("The ServerThread has already been associated with a username. The connection gets terminated now ", e);
                    getServerThread().close("The ServerThread has already been associated with a username");

            } catch (UsernameInvalidException e) {
                int retriesLeft = getRetries() - fails;
                getServerThread().getMessageHandler().handleDebug("A NullPointerException occurred during the user arrangement. The connection get terminated "
                        + ((retriesLeft <= 0) ? "now": ("after " + retriesLeft + " more failed attempt(s)")), e);
                writeJSON(new UsernameResponsePacket(UsernameResponsePacket.UsernameResponseType.ERROR_USERNAME_INVALID, usernameRegexDescription).toJSON());
                if (retriesLeft <= 0) {
                    getServerThread().close("Failed to choose a valid username");
                }
            }  catch (UsernameTakenException e) {
                int retriesLeft = getRetries() - fails;
                getServerThread().getMessageHandler().handleDebug("A NullPointerException occurred during the user arrangement. The connection get terminated "
                        + ((retriesLeft <= 0) ? "now": ("after " + retriesLeft + " more failed attempt(s)")), e);
                writeJSON(new UsernameResponsePacket(UsernameResponsePacket.UsernameResponseType.ERROR_USERNAME_TAKEN, "The username is already taken").toJSON());
                if (retriesLeft <= 0) {
                    getServerThread().close("Failed to choose a non existing username");
                }
            }
        }

            return user;

    }
}
