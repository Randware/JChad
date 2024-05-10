package net.jchad.server.model.command.commands;

import javafx.application.HostServices;
import net.jchad.server.model.server.Server;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class GitHubCommand extends BaseCommand{
    private final String gitHubRepo = "https://github.com/Randware/JChad";

    @Override
    public void execute(Server server, ArrayList<String> args) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(gitHubRepo));
                }
            }
        } catch (Exception ignore) {}

        server.getMessageHandler().handleInfo("Thanks for showing interest in the GitHub project: " + gitHubRepo);
    }
}
