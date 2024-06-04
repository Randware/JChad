package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;
import java.util.ArrayList;

public class GitHubCommand extends BaseCommand{
    private final String gitHubRepo = "https://github.com/Randware/JChad";

    @Override
    public void execute(Server server, ArrayList<String> args) {
        server.getMessageHandler().handleInfo("The official JChad github repository: " + gitHubRepo);
    }
}
