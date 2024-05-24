package net.jchad.server;


import net.jchad.client.view.CLI;
import net.jchad.client.view.gui.GUI;

public class Main {
    private static final boolean cliMode = true;

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
