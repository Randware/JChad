package net.jchad.server.view.cli;

import net.jchad.server.controller.ServerController;
import net.jchad.server.model.common.Util;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.view.cli.terminal.AdvancedTerminal;
import net.jchad.server.view.cli.terminal.SimpleTerminal;
import net.jchad.server.view.cli.terminal.Terminal;
import net.jchad.server.view.cli.terminal.UserExitedException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * TODO: Implement user input reading
 */

// Responsible for displaying server output in CLI mode
public class CLI implements MessageHandler {
    private ServerController server;
    private Terminal terminal;

    public static void main(String[] args) {
        new CLI().runCLI();
    }

    private void runCLI() {
        server = new ServerController(this);
        try {
            terminal = new AdvancedTerminal();
        } catch (IOException e) {
            terminal = new SimpleTerminal();
            handleWarning("Terminal is not supported, using simplified terminal interface");
        }

        server.startServer();

        terminal.initInputReading(this::handleInput, this::handleExit);
    }

    private void handleInput(String command) {
        server.sendCommand(command);
        System.out.println("Sent command: " + command);
    }

    private void handleExit() {
        server.stopServer();
        System.exit(0);
    }

    @Override
    public void handleFatalError(Exception e) {
        terminal.outputMessage("[FATAL ERROR] " + Util.stackTraceToString(e));
        terminal.close();
        server.stopServer();
        System.exit(0);
    }

    @Override
    public void handleError(Exception e) {
        terminal.outputMessage("[ERROR] " + e);
    }

    @Override
    public void handleWarning(String warning) {
        terminal.outputMessage("[WARNING] " + warning);
    }

    @Override
    public void handleInfo(String info) {
        terminal.outputMessage("[INFO] " + info);
    }
}
