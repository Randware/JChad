package net.jchad.client.view.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
    private Stage primaryStage; // Store the primary stage to access it later
    private Scene scene;

    public static void main(String[] args) {
        new GUI().runGUI();
    }

    private void runGUI() {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage; // Assign the primary stage to the class variable
        client = new ClientController(this);

        headerLabel.setText("Welcome Jchader!");
        contentLabel.setText("This is the best Chad-Platform out there");

        Label combinedLabel = new Label(headerLabel.getText() + "\n" + contentLabel.getText());

        Menu connectionsMenu = new Menu("Connections");

        MenuItem connect = new MenuItem("add connection");
        MenuItem disconnect = new MenuItem("remove connection");

        connectionsMenu.getItems().addAll(connect, disconnect);

        menuBar.getMenus().addAll(connectionsMenu);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(menuBar);
        borderPane.setBottom(scrollPane);
        borderPane.setCenter(combinedLabel);

        connect.setOnAction(e -> connect()); // Pass primaryStage to the connect method
        disconnect.setOnAction(e -> client.disconnect());

        this.scene = new Scene(borderPane);

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

    public void connect() { // Modified to accept primaryStage as a parameter
        VBox vbox = new VBox(10);
        Stage dialogStage = new Stage();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label hostLabel = new Label("Host:");
        TextField hostField = new TextField();
        Label portLabel = new Label("Port:");
        TextField portField = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button cancelButton = new Button("Cancel");
        /*cancelButton.setOnAction(e -> {
            // Switch back to the original scene
            primaryStage.setScene(scene); // You'll need to store the original scene somewhere
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();
            primaryStage.setWidth(screenWidth / 2);
            primaryStage.setHeight(screenHeight / 2);
        });*/
        cancelButton.setOnAction(e -> dialogStage.close());

        Button addButton = new Button("Add");
        // addButton functionality goes here

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(cancelButton, addButton);

        grid.add(hostLabel, 0, 1);
        grid.add(hostField, 1, 1);
        grid.add(portLabel, 0, 2);
        grid.add(portField, 1, 2);
        grid.add(nameLabel, 0, 3);
        grid.add(nameField, 1, 3);
        grid.add(passwordLabel, 0, 4);
        grid.add(passwordField, 1, 4);

        /*vbox.getChildren().addAll(grid, buttonContainer);

        Scene connectionScene = new Scene(vbox);
        primaryStage.setScene(connectionScene); // Set the new scene
        primaryStage.sizeToScene(); // Resize the window to fit the new scene*/

        vbox.getChildren().addAll(grid, buttonContainer); // Add the HBox to the VBox instead of individual buttons

        Scene dialogScene = new Scene(vbox); // Removed fixed width and height
        dialogStage.setTitle("Connection Details");
        dialogStage.setScene(dialogScene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.sizeToScene(); // Automatically resize the window to fit its content
        dialogStage.showAndWait();
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
