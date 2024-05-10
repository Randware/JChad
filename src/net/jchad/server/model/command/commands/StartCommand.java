package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.util.ArrayList;

public class StartCommand extends BaseCommand {
    @Override
    public void execute(Server server, ArrayList<String> args) {
        server.runServer();
    }
}
