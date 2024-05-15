package net.jchad.client.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.jchad.client.controller.ClientController;
import net.jchad.client.model.error.MessageHandler;

// Responsible for displaying client output in GUI mode
public class GUI extends Application implements MessageHandler {
    private ClientController client;

    public static void main(String[] args) {
        new GUI().runGUI();
    }

    private void runGUI() {
        client = new ClientController(this);

        // Display GUI
    }



    @Override
    public void handleFatalError(Exception e) {

    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void handleWarning(String warning) {

    }

    @Override
    public void handleInfo(String info) {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(new Label("Hello World")));
        primaryStage.show();
    }
}
