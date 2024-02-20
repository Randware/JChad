package net.jchad.installer.cli;


import net.jchad.installer.config.ConfigUtil;
import net.jchad.installer.config.ConfigManager;

import java.io.Console;
import java.nio.file.Path;
import java.util.function.Predicate;

public class CLI {

    //TODO Add strict mode. When strict mode is enabled the directories will be checked if they exists and wont get creates
    //TODO When strict mode is off folders that don't exists get created
    private Console console = System.console();
    private ConfigManager configManager = new ConfigManager();

    public static final String lineSeparator = System.lineSeparator();
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }


    public void start() {
        System.out.println("""
                
                
                °*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*
                
                                Welcome to the JChad command-line setup!
                                
                °*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*
                        
                """);
        System.out.println("Where should the software be installed? (leave empty for defined path of yaml file)");
        configManager.setDownloadPath(
                reader("> ",
                        (e) -> configManager.isValidPath(e)
                                &&  ConfigUtil.isDirectory(Path.of(e)) ));
        //&& Files.exists(Path.of(e))
        System.out.println("""
                What software do you want to install?
                
                0 - This is the Client and Server software for JChad
                1 - This is the Server software for JChad
                2 - This is the Client software for JChad
                
                """);
        configManager.setSoftwareToInstall(
                Integer.parseInt(
                        reader("> ",
                                (e) -> e.compareTo("/") >= 1
                                        &&  e.compareTo("3") <= -1 && e.length() == 1
                )
        ));
        configManager.process();

    }

    public  String reader(String message, Predicate<String> predicate) {
        String userInput = "";
        while (true) {
            userInput = console.readLine(message);
            if (predicate.test(userInput)) {
                return userInput;
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }


}