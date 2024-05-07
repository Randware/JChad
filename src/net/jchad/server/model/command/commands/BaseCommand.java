package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.util.ArrayList;

public abstract class BaseCommand {
    public abstract void execute(Server server, ArrayList<String> args);
}
