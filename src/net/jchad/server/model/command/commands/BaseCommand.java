package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.util.ArrayList;

/**
 * This class is used as the base class for all commands.
 */
public abstract class BaseCommand {
    public abstract void execute(Server server, ArrayList<String> args);
}
