package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.util.ArrayList;

/**
 * Whitelist one or multiple IP address
 *
 * TODO: Implement proper functionality
 */
public class WhitelistCommand extends BaseCommand {
    @Override
    public void execute(Server server, ArrayList<String> args) {
        server.getMessageHandler().handleInfo("Whitelist command was executed with args: " + args);
    }
}
