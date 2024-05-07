package net.jchad.server.model.command.commands;

import net.jchad.server.model.command.Command;
import net.jchad.server.model.server.Server;

import java.util.ArrayList;

/**
 * Display all available commands with their respective syntax and description.
 */
public class HelpCommand extends BaseCommand {
    @Override
    public void execute(Server server, ArrayList<String> args) {
        StringBuilder helpText = new StringBuilder("Displaying available commands\n\n");

        for(Command c : Command.values()) {
            helpText.append(c.getHelpString())
                    .append("\n");
        }

        server.getMessageHandler().handleInfo(helpText.toString());
    }
}
