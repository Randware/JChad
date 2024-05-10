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

        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        //Really helpful -> https://stackoverflow.com/questions/3571203/what-are-runtime-getruntime-totalmemory-and-freememory


        StringBuilder str = new StringBuilder("Displaying server information\n");

        str.append("Server version → %s\n".formatted(server.getVersion()));
        str.append("Uptime → %s\n".formatted(uptime));
        str.append("RAM usage → %s MB /%s MB\n".formatted(usedMemory, maxMemory));
        str.append("Port → %s\n".formatted(server.getConfig().getServerSettings().getPort()));
        str.append("Connected → %s".formatted(server.getMainSocket().getServerThreadSet().size())); //Set does not get higher than 20. Why?

        server.getMessageHandler().handleInfo(str.toString());
    }
}
