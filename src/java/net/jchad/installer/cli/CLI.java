package net.jchad.installer.cli;


import net.jchad.installer.config.ConfigUtil;
import net.jchad.installer.config.ConfigManager;
import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.BarDisplay;

import java.io.Console;
import java.nio.file.Path;
import java.util.function.Predicate;

public class CLI implements BarDisplay {


    private Console console = System.console();
    private ConfigManager configManager = new ConfigManager();

    public static final String lineSeparator = System.lineSeparator();
    private  long progressForChar = 0;
    public static void main(String[] args) {

        CLI cli = new CLI();
        cli.hook("RepoDownloader");
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
        } catch (Exception e) { //If an error occurred
            System.out.println("\n\n" + "\u001B[31m" + "-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_");
            System.out.println("\nFata error occurred!");
            System.out.println("Unsaved yml file may be lost and the unfinished download(s) may be corrupt, be careful! \n");
            System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_" + "\u001B[0m \n");
            e.printStackTrace(System.out);
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

        //System.out.printf("\r[%s/%s]", bar.getCurrentProgress(), bar.getMaxProgress());
    }

    @Override
    public void updateOnFailed(Bar bar) {

    }

    @Override
    public void updateOnStart(Bar bar) {
        progressForChar = bar.getMaxProgress() / 80;
        System.out.print("\033[38;5;46m");
    }

    @Override
    public void updateOnEnd(Bar bar) {
        //
        System.out.println("\033[0m");
    }

    @Override
    public void updateOnUpdate(Bar bar) {
        int currentChars = (int) (bar.getCurrentProgress() / progressForChar);
        int leftProgress = 80 - currentChars;
        System.out.print("\r[" + "#".repeat(currentChars) + "=".repeat(leftProgress) + "]");
        //System.out.println("Updated!");
    }
}