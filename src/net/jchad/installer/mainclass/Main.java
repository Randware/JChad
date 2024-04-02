package net.jchad.installer.mainclass;

import net.jchad.installer.cli.CLI;
import net.jchad.installer.gui.GUI;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-cl")) {
            // Launch console interface
            CLI.main(args);
        } else {
            // Launch JavaFX GUI interface
            GUI.main(args);
        }
    }
}

