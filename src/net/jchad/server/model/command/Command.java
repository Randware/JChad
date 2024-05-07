package net.jchad.server.model.command;

import net.jchad.server.model.command.commands.*;
import net.jchad.server.model.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Command {
    STOP("stop", new StopCommand()),
    WHITELIST("whitelist", new WhitelistCommand()),
    BLACKLIST("blacklist", new BlacklistCommand()),
    KICK("kick", new KickCommand());

    private String commandString;
    private BaseCommand commandObject;

    Command(String commandString, BaseCommand commandObject) {
        this.commandString = commandString;
        this.commandObject = commandObject;
    }

    public void execute(Server server, ArrayList<String> args) {
        commandObject.execute(server, args);
    }

    public static void executeCommand(String command, Server server) {
        String[] splitCommand = command.split(" ");
        String mainCommand = null;
        String[] argsCommand = null;

        if(splitCommand.length == 0) {
            return;
        }

        if(splitCommand.length == 1) {
            mainCommand = splitCommand[0];
        }

        if(splitCommand.length > 1) {
            mainCommand = splitCommand[0];
            argsCommand = Arrays.copyOfRange(splitCommand, 1, splitCommand.length);
        }

        for(Command c : values()) {
            if(c.getCommandString().equalsIgnoreCase(mainCommand)) {
                if(argsCommand != null) {
                    c.execute(server, new ArrayList<>(List.of(argsCommand)));
                } else {
                    c.execute(server, null);
                }
                return;
            }
        }

        server.getMessageHandler().handleInfo("\"" + mainCommand + "\" command not found");
    }

    public String getCommandString() {
        return commandString;
    }

    public BaseCommand getCommandObject() {
        return commandObject;
    }
}
