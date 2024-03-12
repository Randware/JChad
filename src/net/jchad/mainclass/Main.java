package net.jchad.mainclass;

import net.jchad.installer.cli.CLI;
import net.jchad.installer.gui.GUI;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("console")) {
            // Launch console interface
            CLI.main(new String[]{});
        } else {
            // Launch JavaFX GUI interface
            GUI.main(new String[]{});
        }
    }
}

