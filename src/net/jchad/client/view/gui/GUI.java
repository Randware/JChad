package net.jchad.client.view.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChatMessage;

import java.rmi.RemoteException;

public class GUI extends Application implements ViewCallback {
    private ClientController client;
    private MenuBar menuBar = new MenuBar();
    private ScrollPane scrollPane = new ScrollPane();
    private Label headerLabel = new Label();
    private Label contentLabel = new Label();

    public static void main(String[] args) {
        new GUI().runGUI();
    }

    private void runGUI() {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        client = new ClientController(this);

        headerLabel.setText("Welcome Jchader!");
        contentLabel.setText("This is the best Chad-Platform out there");

        // Combine labels or use a different layout strategy here
        Label combinedLabel = new Label(headerLabel.getText() + "\n" + contentLabel.getText());

        Menu connectionsMenu = new Menu("Connections");

        MenuItem connect = new MenuItem("add connection");
        MenuItem disconnect = new MenuItem("remove connection");
        MenuItem currentConnection = new MenuItem("current connection");

        connectionsMenu.getItems().addAll(connect, disconnect, currentConnection);

        menuBar.getMenus().addAll(connectionsMenu);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane borderPane = new BorderPane();

        //VBox scrollPaneVBox = new VBox();

        borderPane.setTop(menuBar);
        borderPane.setBottom(scrollPane);//scrollPaneVBox
        borderPane.setCenter(combinedLabel); // Use the combined label here

        Scene scene = new Scene(borderPane);

        // Calculate 50% of the screen size
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        primaryStage.setWidth(screenWidth / 2);
        primaryStage.setHeight(screenHeight / 2);

        primaryStage.setTitle("JChad Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        handleError(new RuntimeException("This is an error"));
        handleFatalError(new RemoteException("This is a FatalError"));
        handleInfo("This is a Info");
        handleWarning("This is a Warning");
    }

    @Override
    public void handleFatalError(Exception e) {
        new ClientAlerts(Alert.AlertType.ERROR, "Fatal Error", e.toString());
    }

    @Override
    public void handleError(Exception e) {
        new ClientAlerts(Alert.AlertType.ERROR, "Error", e.toString());
    }

    @Override
    public void handleWarning(String warning) {
        new ClientAlerts(Alert.AlertType.WARNING, "Warning", warning);
    }

    @Override
    public void handleInfo(String info) {
        new ClientAlerts(Alert.AlertType.INFORMATION, "Info", info);
    }

    /**
     * Hier habe ich bereits das prompt implementiert, damit ihr euch vorstellen könnt wie man mit mehreren
     * Klassen im GUI arbeiten kann. Das Ganze macht den Code unendlich lesbarer und auch einfacher wiederzuverwenden.
     * Versucht das ganze GUI so ähnlich aufzubauen.
     */
    @Override
    public String displayPrompt(String promptTitle, String promptMessage) {
        InputPrompt inputPrompt = new InputPrompt(Alert.AlertType.CONFIRMATION, promptTitle, promptMessage);

        inputPrompt.showAndWait();

        String input = inputPrompt.getInput();

        if(input != null && !input.isEmpty()) {
            return input;
        } else {
            // disconnect the currently running connection or connection process,
            // since the user didn't provide the required information.
            client.disconnect();

            return null;
        }
    }

    @Override
    public void displayOwnMessage(ClientChatMessage message) {

    }

    @Override
    public void displayOtherMessage(ClientChatMessage message) {

    }

    @Override
    public void updateDisplay() {

    }
}
