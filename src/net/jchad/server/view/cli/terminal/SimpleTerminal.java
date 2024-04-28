package net.jchad.server.view.cli.terminal;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * This {@link Terminal} implementation is an "simple" terminal,
 * which is does not use features such as ANSI escape sequences and cursor movements.
 * It only uses a very simple output and input format, which may not look as good,
 * but should be supported by most, if not all terminals
 *
 * TODO: Fix input prompt, as it interferes with the output.
 */
public class SimpleTerminal extends Terminal {
    private Scanner scanner;
    private Thread inputReader;


    private boolean run;

    public SimpleTerminal() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void outputMessage(String message) {
        System.out.println("\r" + message);
    }

    @Override
    public void initInputReading(Consumer<String> inputHandler) {
        run = true;

        inputReader = new Thread(() -> {
            while (run) {
                System.out.print("> ");

                String input = scanner.nextLine().trim();

                if(!input.isBlank()) {
                    inputHandler.accept(input);
                }
            }
        });

        inputReader.start();
    }

    @Override
    public void close() {
        this.run = false;
        inputReader.interrupt();
    }
}
