package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.util.ArrayList;

/**
 * Kick one, multiple or all connected clients from the server.
 *
 * TODO: Implement proper functionality
 */
public class KickCommand extends BaseCommand {
    @Override
    public void execute(Server server, ArrayList<String> args) {
        if(args != null && !args.isEmpty()) {
            server.getMainSocket().getServerThreadSet().forEach(serverThread -> {
                if(args.contains(serverThread.getRemoteAddress()) || args.getFirst().equals("all")) {
                    serverThread.close("You were kicked from the server.", false);
                }
            });

            server.getMessageHandler().handleInfo("Successfully kicked all specified clients");
        } else {
            server.getMessageHandler().handleInfo("Please supply at least one IP address to the kick command");
        }

    }
}
