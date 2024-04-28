package net.jchad.client;

import net.jchad.server.view.cli.CLI;
import net.jchad.server.view.GUI;

public class Main {
    private final static boolean cliMode = true;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-cl") || cliMode) {
            // Launch console interface
            CLI.main(args);
        } else {
            // Launch JavaFX GUI interface
            GUI.main(args);
        }
    }
}
