package net.jchad.client;


import net.jchad.client.view.CLI;
import net.jchad.client.view.gui.GUI;

public class Main {
    private final static boolean cliMode = true;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-cl") || cliMode) {
            CLI.main(args);
        } else {
            GUI.main(args);
        }
    }
}
