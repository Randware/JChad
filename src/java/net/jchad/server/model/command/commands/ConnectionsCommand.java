package net.jchad.server.model.command.commands;

import net.jchad.server.model.server.Server;
import net.jchad.server.model.server.ServerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsCommand extends BaseCommand{
    @Override
    public void execute(Server server, ArrayList<String> args) {
        if (args == null || args.getFirst().equalsIgnoreCase("help") ||args.getFirst().equalsIgnoreCase("-h") ) {
            server.getMessageHandler().handleInfo("""
                    Usage:
                    connections list | lists all connections
                    connections kick (-user) <ip1/username1> <ip2/username2> | kicks the provided ip or user (if specified).
                    connections help | shows you more infos about the connections command
                    """);
        } else {
            if (args.getFirst().equalsIgnoreCase("list")) {
                server.getMessageHandler().handleInfo(displayConnections(server));
                return;
            }
            if (args.getFirst().equalsIgnoreCase("kick")) {
                server.getMessageHandler().handleInfo(kickConnections(server, args));
                return;
            }
        }
    }

    public String displayConnections(Server server) {
        StringBuilder connections = new StringBuilder();
        connections.append("All connections:\n");
        for (ServerThread thread : server.getMainSocket().getServerThreadSet()) {
            connections.append("%-32s %-20s %s%n".formatted(thread.getRemoteAddress(), (thread.getUser() != null) ? thread.getUser().getUsername() : "N/A",
                    (thread.getUser() != null && thread.getUser().isReadyToReceiveMessages()) ? "Connection established" : "Connecting process"));
        }
        return connections.toString();
    }

    public String kickConnections(Server server, ArrayList<String> args) {
    if (args.size() > 1) {
        if (args.get(1).equalsIgnoreCase("-u") || args.get(1).equalsIgnoreCase("-user")) {
                AtomicInteger usersKicked = new AtomicInteger(0);
                List<String> usernamesToKick = args.subList(2, args.size());
                server.getMainSocket().getServerThreadSet()
                        .stream()
                        .filter(t -> t.getUser() != null && usernamesToKick.contains(t.getUser().getUsername()))
                        .forEach(t -> {
                            t.close("You were kicked from the server.", false);
                            usersKicked.getAndIncrement();
                        });
                return usersKicked.get() + " users were kicked";


        }

            AtomicInteger ipsKicked = new AtomicInteger(0);
            List<String> ipsToKick = args.subList(1, args.size());
            server.getMainSocket().getServerThreadSet()
                    .stream()
                    .filter(t -> t.getUser() != null && ipsToKick.contains(t.getRemoteAddress().split(":")[0]))
                    .forEach(t -> {
                        t.close("You were kicked from the server.", false);
                        ipsKicked.getAndIncrement();
                    });
            return ipsKicked.get() + " connections were kicked";



    }

    if (args.size() == 1) return "Please specify the ips to kick. Do \"connections help\" to see proper use";
        return "Unrecognized attribute(s). Do \"connections help\"";
    }
}
