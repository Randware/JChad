package net.jchad.client.view.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.client.model.store.connection.ConnectionDetailsBuilder;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GUI extends Application implements ViewCallback {
    private ClientController client;
    private MenuBar menuBar = new MenuBar();
    private ScrollPane scrollPane = new ScrollPane();
    private Label headerLabel = new Label();
    private Label contentLabel = new Label();
    private Label combinedLabel = new Label();
    private Stage primaryStage; // Store the primary stage to access it later
    private Scene scene;
    private Stage dialogStage;
    private double sizeValue;
    private BorderPane borderPane = new BorderPane();
    private TextArea chatArea = new TextArea();
    private ConnectionDetailsBuilder connectionDetailsBuilder;
    private String selectedChat;
    private final KeyCombination crtlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage; // Assign the primary stage to the class variable
        client = new ClientController(this);

        headerLabel.setText("Welcome Jchader!");
        contentLabel.setText("This is the best Chad-Platform out there");

        combinedLabel = new Label(headerLabel.getText() + "\n" + contentLabel.getText());

        Menu connectionsMenu = new Menu("Connections");

        MenuItem connect = new MenuItem("add connection");
        MenuItem disconnect = new MenuItem("remove connection");

        connectionsMenu.getItems().addAll(connect, disconnect);

        Menu settingsMenu = new Menu("Settings");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size (ctrl & +)");
        MenuItem standardFontSize = new MenuItem("standard Font size (crtl & R)");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size (crtl & -)");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);

        menuBar.getMenus().addAll(connectionsMenu, settingsMenu);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        double baseFontSize = screenWidth * 0.007;
        double windowWidth = screenWidth * 0.5;
        double windowHeight = screenHeight * 0.5;

        this.sizeValue = baseFontSize;

        borderPane = new BorderPane();

        borderPane.setTop(menuBar);
        borderPane.setBottom(scrollPane);
        borderPane.setCenter(combinedLabel);

        connect.setOnAction(e -> connect()); // Pass primaryStage to the connect method
        disconnect.setOnAction(e -> client.disconnect());
        increaseFontSize.setOnAction(e -> changeFontSize(2));
        standardFontSize.setOnAction(e -> standardFontSizeMethod());
        decreaseFontSize.setOnAction(e -> changeFontSize(-2));

        borderPane.requestFocus();

        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (crtlPlus.match(event)) {
                changeFontSize(2);
                event.consume();
            } else if (crtlMinus.match(event)) {
                changeFontSize(-2);
                event.consume();
            } else if (crtlR.match(event)) {
                standardFontSizeMethod();
                event.consume();
            }
        });

        this.scene = new Scene(borderPane, windowWidth, windowHeight);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        primaryStage.setWidth(screenWidth / 2);
        primaryStage.setHeight(screenHeight / 2);

        //primaryStage.getIcons().add(new Image("file:/Pfad/zum/Bild.png"));

        primaryStage.setTitle("JChad Client");
        primaryStage.setScene(scene);
        primaryStage.show();
        standardFontSizeMethod();

    }

    public void connect() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        dialogStage = new Stage();

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
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        portField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            char ar[] = e.getCharacter().toCharArray();
            boolean b = ar[0] >= 48 && ar[0] <= 57; // ASCII values for digits 0-9
            if (!b) {
                e.consume(); // Ignore event, don't type anything
            }
        });

        Button cancelButton = new Button("Cancel");
        /*cancelButton.setOnAction(e -> {
            // Switch back to the original scene
            primaryStage.setScene(scene); // You'll need to store the original scene somewhere
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();
            primaryStage.setWidth(screenWidth / 2);
            primaryStage.setHeight(screenHeight / 2);
        });*/

        Button addButton = new Button("Connect");
        Button saveButton = new Button("Add");

        addButton.setOnAction(e -> newConnection(hostField.getText(), portField.getText(), nameField.getText(), passwordField.getText(), usernameField.getText(), true));
        cancelButton.setOnAction(e -> dialogStage.close());
        saveButton.setOnAction(e -> newConnection(hostField.getText(), portField.getText(), nameField.getText(), passwordField.getText(), usernameField.getText(), false));

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(cancelButton, addButton, saveButton);

        grid.add(hostLabel, 0, 1);
        grid.add(hostField, 1, 1);
        grid.add(portLabel, 0, 2);
        grid.add(portField, 1, 2);
        grid.add(nameLabel, 0, 3);
        grid.add(nameField, 1, 3);
        grid.add(passwordLabel, 0, 4);
        grid.add(passwordField, 1, 4);
        grid.add(usernameLabel, 0, 5);
        grid.add(usernameField, 1, 5);

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

    private void newConnection(String Host, String Port, String ConnectionName, String Password, String Username, Boolean connect) {
        if (Host == null || Host.isEmpty()) {
            handleWarning("Host cannot be empty");
            return;
        }

        if (Port == null || Port.isEmpty()) {
            handleWarning("Port cannot be empty");
            return;
        }

        if (ConnectionName == null || ConnectionName.isEmpty()) {
            handleWarning("ConnectionName cannot be empty");
            return;
        }

        if (Username == null || Username.isEmpty()) {
            handleWarning("Username cannot be empty");
            return;
        }

        connectionDetailsBuilder = new ConnectionDetailsBuilder();
        connectionDetailsBuilder.addConnectionName(ConnectionName);
        connectionDetailsBuilder.addHost(Host);
        connectionDetailsBuilder.addPassword(Password);
        connectionDetailsBuilder.addPort(Integer.parseInt(Port));
        connectionDetailsBuilder.addUsername(Username);

        if (connect){
            System.out.println("connect true");
            client.connect(connectionDetailsBuilder.build());
            showChatSelectionWindow();

            handleInfo("Successfully connected to: " + selectedChat);
        }else {
            System.out.println("connect false");
            client.configuration().addConnection(connectionDetailsBuilder.build());
        }

        dialogStage.close();
    }

    public void changeToChat() {
        // Create a new layout for the chat view
        BorderPane chatLayout = new BorderPane();

        // Reuse the existing menu bar
        chatLayout.setTop(menuBar);

        // Assuming you have a TextArea for displaying chat messages
        chatArea.setEditable(false); // Make the chat area read-only
        chatLayout.setCenter(chatArea);

        // Create a new scene with the chat layout
        Scene chatScene = new Scene(chatLayout, scene.getWidth(), scene.getHeight());

        // Apply the scene to the primary stage
        primaryStage.setScene(chatScene);

        System.out.println("changed to chat");
    }

    private void showChatSelectionWindow() {
        // Create a new stage for the chat selection window
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Select Chat");

        // Initialize the dialog stage
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);

        // Fetch available chats
        ArrayList<ClientChat> chats = client.getChats();

        // Create a ListView to display the chats
        ListView<String> chatListView = new ListView<>();
        ObservableList<String> chatNames = FXCollections.observableArrayList(chats.stream()
                .map(ClientChat::getName)
                .collect(Collectors.toList()));
        chatListView.setItems(chatNames);

        // Buttons for Select and Cancel actions
        Button selectButton = new Button("Select");
        Button cancelButton = new Button("Cancel");

        // Action for Select button
        selectButton.setOnAction(event -> {
            selectedChat = chatListView.getSelectionModel().getSelectedItem();
            if (selectedChat!= null) {
                // Handle the selected chat here
                changeToChat();
                System.out.println("Selected Chat: " + selectedChat);
                dialogStage.close();
            }
        });

        // Action for Cancel button
        cancelButton.setOnAction(event -> dialogStage.close());

        // Layout for the buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getChildren().addAll(selectButton, cancelButton);

        // Main layout for the dialog
        VBox dialogLayout = new VBox(20);
        dialogLayout.getChildren().addAll(chatListView, buttonContainer);
        dialogLayout.setPadding(new Insets(10));

        // Scene for the dialog
        Scene dialogScene = new Scene(dialogLayout, 300, 200);

        // Set the scene on the dialog stage
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void changeFontSize(int size) {
        this.sizeValue = sizeValue + size;
        if (sizeValue < 0) {
            this.sizeValue = 1;
        } else if (sizeValue > 50) {
            this.sizeValue = 50;
        }
        updateWindowFontSizeEverything(sizeValue);
    }

    private void standardFontSizeMethod() {
        this.sizeValue = screenWidth * 0.01;
        updateWindowFontSizeEverything(sizeValue);
    }

    public void updateWindowFontSizeEverything(double fontSize) {
        menuBar.setStyle("-fx-font-size: " + fontSize);
        headerLabel.setStyle("-fx-font-size: " + fontSize);
        contentLabel.setStyle("-fx-font-size: " + fontSize);
        combinedLabel.setStyle("-fx-font-size: " + fontSize);
        scrollPane.setStyle("-fx-font-size: " + fontSize);
    }

    private void displaySavedConnections() {
        ArrayList<ConnectionDetails> connections = client.configuration().getConnections();
        VBox connectionsContainer = new VBox(10); // Container for all connection boxes
        connectionsContainer.setPadding(new Insets(10)); // Padding around the container

        for (ConnectionDetails connection : connections) {
            VBox connectionBox = new VBox(5); // Individual connection box
            connectionBox.setPadding(new Insets(10)); // Padding inside each box
            connectionBox.setStyle("-fx-border-color: black;"); // Optional: adds a border around each box

            // Create labels for each piece of connection information
            Label hostLabel = new Label("Host: " + connection.getHost());
            Label portLabel = new Label("Port: " + connection.getPort());
            Label nameLabel = new Label("Name: " + connection.getConnectionName());
            Label usernameLabel = new Label("Username: " + connection.getUsername());
            Label passwordLabel = new Label("Password: " + connection.getPassword());

            // Add text alignment for better visual appearance
            hostLabel.setTextAlignment(TextAlignment.LEFT);
            portLabel.setTextAlignment(TextAlignment.LEFT);
            nameLabel.setTextAlignment(TextAlignment.LEFT);
            usernameLabel.setTextAlignment(TextAlignment.LEFT);
            passwordLabel.setTextAlignment(TextAlignment.LEFT);

            // Add labels to the connection box
            connectionBox.getChildren().addAll(hostLabel, portLabel, nameLabel, usernameLabel, passwordLabel);

            // Add the connection box to the container
            connectionsContainer.getChildren().add(connectionBox);
        }

        // Update the scroll pane content
        scrollPane.setContent(connectionsContainer);
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
