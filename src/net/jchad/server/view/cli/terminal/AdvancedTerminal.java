package net.jchad.server.view.cli.terminal;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * This {@link Terminal} implementation is an "advanced" terminal,
 * that supports features such as ANSI escape sequences and cursor movements.
 */
public class AdvancedTerminal extends Terminal {
    /**
     * The {@link LineReader} instance used to read input and display output in a nicely formatted way.
     */
    private final LineReader reader;

    /**
     * While this is true the input reading runs
     */
    private boolean run;

    /**
     * Default constructor.
     *
     * @throws IOException if the current terminal is not supported
     */
    public AdvancedTerminal() throws IOException {
        reader = LineReaderBuilder.builder().terminal(TerminalBuilder.builder().dumb(true).build()).build();
    }

    @Override
    public void outputMessage(String message) {
        reader.printAbove("\r" + message);
    }

    @Override
    public void initInputReading(Consumer<String> inputHandler, Runnable exitHandler){
        run = true;
        while (run) {
            String input;
            try {
                 input = reader.readLine("> ").trim();
            } catch (UserInterruptException e) {
                clearPreviousLine();
                exitHandler.run();
                return;
            }

            if (!input.isBlank()) {
                inputHandler.accept(input);
            } else {
                clearPreviousLine();
            }
        }
    }

    @Override
    public void close() {
        this.run = false;
    }

    private void clearPreviousLine() {
        reader.getTerminal().puts(InfoCmp.Capability.cursor_up);
    }
}
