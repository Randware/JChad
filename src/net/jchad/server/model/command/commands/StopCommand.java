package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.util.ArrayList;

/**
 * Stop the server
 *
 * TODO: Implement proper server stopping functionality
 */
public class StopCommand extends BaseCommand {

    @Override
    public void execute(Server server, ArrayList<String> args) {
        server.getMessageHandler().handleInfo("Stop command was executed with args: " + args);
    }
}
