package net.jchad.server.view;

import net.jchad.server.controller.ServerController;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.shared.io.terminal.AdvancedTerminal;
import net.jchad.shared.io.terminal.SimpleTerminal;
import net.jchad.shared.io.terminal.Terminal;
import net.jchad.shared.common.Util;

import java.io.IOException;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;

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
    }

    private void handleExit() {
        if(server.isRunning()) {
            server.stopServer();
        }

        System.exit(0);
    }

    @Override
    public void handleFatalError(Exception e) {
        terminal.outputMessage(ansi().fg(RED).boldOff().a("[FATAL ERROR] ").reset()
                + Util.stackTraceToString(e));
        terminal.close();
        server.stopServer();
    }

    @Override
    public void handleError(Exception e) {
        terminal.outputMessage(ansi().fg(RED).bold().a("[ERROR] ").reset().toString() + Util.stackTraceToString(e));
    }

    @Override
    public void handleWarning(String warning) {
        terminal.outputMessage(ansi().fg(YELLOW).a("[WARNING] ").reset() + warning);
    }

    @Override
    public void handleInfo(String info) {
        terminal.outputMessage(ansi().fg(CYAN).a("[INFO] ").reset() + info);
    }

    @Override
    public void handleDebug(String debug) {
        // Only log debug messages if debug mode is enabled in server settings
        if(!server.getServerConfig().getServerSettings().isDebugMode()) return;

        terminal.outputMessage("[DEBUG] " + debug);
    }
}
