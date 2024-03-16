package net.jchad.installer.cli;


import net.jchad.installer.config.ConfigUtil;
import net.jchad.installer.config.ConfigManager;
import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.BarDisplay;
import net.jchad.installer.core.progressBar.BarStatus;
import net.jchad.installer.core.progressBar.BarUpdater;

import java.io.Console;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.function.Predicate;

public class CLI implements BarDisplay {


    private Console console = System.console();
    private ConfigManager configManager = new ConfigManager();

    public static final String lineSeparator = System.lineSeparator();
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }


    public void start() {
        try {
            System.out.println("""
                
                
                °*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*
                
                                Welcome to the JChad command-line setup!
                                
                °*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*°*°+°*°*°*°*°*
                        
                """);
            System.out.println("Where should the software be installed? (leave empty for current or set path)");
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
        } catch (NullPointerException e) { //If the user ends the program abruptly
            System.out.println("\n\n" + "\u001B[31m" + "-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
            System.err.println("\nProgram stopped abruptly!");
            System.err.println("Unsaved yml file may be lost and the unfinished download(s) may be corrupt, be careful! \n");
            System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_" + "\u001B[0m \n");
        }

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


    @Override
    public void update(Bar bar) {
        System.out.printf("%s/%s%n", bar.getCurrentProgress(), bar.getMaxProgress());
    }
}