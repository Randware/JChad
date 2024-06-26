package net.jchad.installer;

import net.jchad.installer.cli.CLI;
import net.jchad.installer.gui.GUI;

public class Main {
    private static final boolean cliMode = false;

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

