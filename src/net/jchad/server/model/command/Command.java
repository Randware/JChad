package net.jchad.server.model.command;

import net.jchad.server.model.command.commands.*;
import net.jchad.server.model.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This enum stores all commands and their relevant information.
 * It makes creating and modifying commands easy and maintainable.
 */
public enum Command {
    STOP("stop", new StopCommand(), "stop", "Stop the server"),
    WHITELIST("whitelist", new WhitelistCommand(), "whitelist <IPv4/IPv6> <IPv4/IPv6> ...", "Whitelist one or multiple IP addresses"),
    BLACKLIST("blacklist", new BlacklistCommand(), "blacklist <IPv4/IPv6]> <IPv4/IPv6> ...", "Blacklist one or multiple IP addresses"),
    KICK("kick", new KickCommand(), "kick <IPv4/IPv6>, <all>", "Kick specific IP addresses or all connected clients"),
    HELP("help", new HelpCommand(), "help", "Show the help dialog");

    /**
     * This value represents the command string, which should be associated with the command.
     * For example, the help command should be callable with the "help" command. Crazy, right?
     */
    private String commandString;

    /**
     * This is the command object, which will be called when this command
     * is executed. To create your own commands, simply extend from the {@link BaseCommand} class.
     */
    private BaseCommand commandObject;

    /**
     * A short and descriptive string, representing the expected syntax for this command.
     * It should be written in an opinionated way, to make everything look uniform.
     */
    private String usage;

    /**
     * A short and simple description of this command.
     */
    private String description;


    /**
     * This stores the help string which will be displayed for help for this specific command.
     * It is built automatically in the constructor.
     */
    private String helpString;

    /**
     *
     * @param commandString the command string, which should be associated with the command
     * @param commandObject the command object, which will be called when this command
     * @param usage a short and descriptive string, representing the expected syntax for this command
     * @param description a short and simple description of this command
     */
    Command(String commandString, BaseCommand commandObject, String usage, String description) {
        this.commandString = commandString;
        this.commandObject = commandObject;
        this.usage = usage;
        this.description = description;
        
        this.helpString = buildHelpString();
    }

    /**
     * Build the help string for this command.
     *
     * @return the generated help string
     */
    private String buildHelpString() {
        String str = """
                 "%s"
                  └ %s
                """.formatted(usage, description);

        return str;
    }

    /**
     * This method is used for executing the corresponding command with the given arguments,
     * from a simple command string the user entered.
     *
     * @param command the command string which was entered by the user
     * @param server the {@link Server} instance in which this command will be executed
     */
    public static void executeCommandString(String command, Server server) {
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

    /**
     * This will execute the specific command this was called on.
     *
     * @param server the {@link Server} instance in which this command will be executed
     * @param args the arguments that should be supplied to this command
     */
    public void execute(Server server, ArrayList<String> args) {
        commandObject.execute(server, args);
    }

    /**
     * @return the command string (or name) this command is associated with
     */
    public String getCommandString() {
        return commandString;
    }

    /**
     * @return the command object which will be executed for this specific command
     */
    public BaseCommand getCommandObject() {
        return commandObject;
    }

    /**
     * @return a description of what the command syntax should look like
     */
    public String getUsage() {
        return usage;
    }

    /**
     * @return a short description of this command
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the formatted help string for this command
     */
    public String getHelpString() {
        return helpString;
    }
}
