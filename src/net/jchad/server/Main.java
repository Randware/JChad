package net.jchad.server;


import net.jchad.server.view.CLI;
import net.jchad.server.view.GUI;

public class Main {
    private static final boolean cliMode = true;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-cl") || cliMode) {
            // Launch console interface
            CLI.main(args);
        } /*else if(args.length > 0 && args[0].equals("-new")) {
            // Launch JavaFX GUI interface
            net.jchad.server.view.dashboard.GUI.main(args);
        }*/ else {
            GUI.main(args);
        }
    }
}
