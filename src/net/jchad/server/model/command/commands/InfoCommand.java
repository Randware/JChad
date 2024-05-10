package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class InfoCommand extends BaseCommand {
    @Override
    public void execute(Server server, ArrayList<String> args) {
        String uptime = LocalDateTime.ofEpochSecond(
                        Duration.between(
                                Instant.ofEpochMilli(server.getStartTimestamp()),
                                Instant.now()).getSeconds(), 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        long totalMemoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);


        StringBuilder str = new StringBuilder("Displaying server information\n");

        str.append("Server version → %s\n".formatted(server.getVersion()));
        str.append("Uptime → %s\n".formatted(uptime));
        str.append("RAM usage → %sMB\n".formatted(totalMemoryUsage));
        str.append("Port → %s\n".formatted(server.getConfig().getServerSettings().getPort()));

        server.getMessageHandler().handleInfo(str.toString());
    }
}
