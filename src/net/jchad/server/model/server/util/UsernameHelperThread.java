package net.jchad.server.model.server.util;

import net.jchad.server.model.server.ServerThread;
import net.jchad.server.model.users.ConnectionExistsException;
import net.jchad.server.model.users.User;
import net.jchad.server.model.users.UsernameInvalidException;
import net.jchad.server.model.users.UsernameTakenException;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.username.UsernameServerPacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;

public class UsernameHelperThread extends HelperThread {
    private final String usernameRegexDescription;

    public UsernameHelperThread(ServerThread serverThread) {
        super(serverThread);
        usernameRegexDescription = serverThread.getConfig().getInternalSettings().getUsernameRegexDescription();
    }

    public User arrangeUser() {
        getServerThread().getMessageHandler().handleDebug("%s started the UsernameHelperThread".formatted(getServerThread().getRemoteAddress()));
        User user = null;
        writeJSON(new UsernameServerPacket(UsernameServerPacket.UsernameResponseType.PROVIDE_USERNAME, "Please enter a username.").toJSON());
        for (int fails = 0; fails <= getRetries(); fails++) {
            UsernameClientPacket usernameClientPacket = readJSON(UsernameClientPacket.class, PacketType.USERNAME);
            try {

                user = new User(usernameClientPacket.getUsername(), getServerThread());
                getServerThread().getPrintWriter().println(new UsernameServerPacket(UsernameServerPacket.UsernameResponseType.SUCCESS_USERNAME_SET, "The username was successfully set").toJSON());
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
                getServerThread().getMessageHandler().handleDebug("The client entered an invalid username. The connection get terminated "
                        + ((retriesLeft <= 0) ? "now": ("after " + retriesLeft + " more failed attempt(s)")), e);
                writeJSON(new UsernameServerPacket(UsernameServerPacket.UsernameResponseType.ERROR_USERNAME_INVALID, usernameRegexDescription).toJSON());
                if (retriesLeft <= 0) {
                    getServerThread().close("Failed to choose a valid username");
                }
            }  catch (UsernameTakenException e) {
                int retriesLeft = getRetries() - fails;
                getServerThread().getMessageHandler().handleDebug("The client tried to get an existing username. The connection get terminated "
                        + ((retriesLeft <= 0) ? "now": ("after " + retriesLeft + " more failed attempt(s)")), e);
                writeJSON(new UsernameServerPacket(UsernameServerPacket.UsernameResponseType.ERROR_USERNAME_TAKEN, "The username is already taken").toJSON());
                if (retriesLeft <= 0) {
                    getServerThread().close("Failed to choose a non existing username");
                }
            }
        }
        getServerThread().getMessageHandler().handleDebug("%s selected a username successfully".formatted(getServerThread().getRemoteAddress()));
            return user;

    }
}
