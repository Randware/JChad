package net.jchad.installer.cli;

import java.util.HashMap;
@Deprecated
public class Parser {
    private static final HashMap<String, String> mapArgs = new HashMap<>();
    static HashMap<String , String> argumentsParser(String[] args) {
        for (int i = 0; i < args.length-1; i++) {
            if (args[i].trim().startsWith("--")) {
                if (!args[i+1].trim().startsWith("--")) {
                    mapArgs.put(args[i].trim(), args[i+1].trim());
                    i += 1;
                }
            }
        }
        return new HashMap<>(mapArgs);
    }
}