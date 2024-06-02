package net.jchad.client.view.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
import net.jchad.client.view.videos.VidPlayer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class GUI extends Application implements ViewCallback {
    private ClientController client = new ClientController(this);
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
    private VidPlayer vidPlayer = new VidPlayer();
    private final KeyCombination crtlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();
    TextArea chatDisplayArea = new TextArea();
    TextField messageInputField = new TextField();
    HBox chatline = new HBox();
    VBox theGuyIamChattingWith = new VBox();
    VBox me = new VBox();
    ScrollPane chatScrollPane;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage; // Assign the primary stage to the class variable

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
        disconnect.setOnAction(e -> disconnect());
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
        primaryStage.setWidth(screenWidth / 1.5);
        primaryStage.setHeight(screenHeight / 1.5);

        //primaryStage.getIcons().add(new Image("file:/Pfad/zum/Bild.png"));

        primaryStage.setTitle("JChad Client");
        primaryStage.setScene(scene);
        primaryStage.show();
        standardFontSizeMethod();
        displaySavedConnections();

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

        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(hostLabel, 0, 2);
        grid.add(hostField, 1, 2);
        grid.add(portLabel, 0, 3);
        grid.add(portField, 1, 3);
        grid.add(usernameLabel, 0, 4);
        grid.add(usernameField, 1, 4);
        grid.add(passwordLabel, 0, 5);
        grid.add(passwordField, 1, 5);

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
        if (Objects.equals(Host, "monke")) {
            vidPlayer.start(primaryStage); // Start the VidPlayer when the host is "monke"
            dialogStage.close();
            return;
        }

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
            displaySavedConnections();
        }

        dialogStage.close();
    }

    public void changeToChat() {
        Button sendButton = new Button("Send");
        sendButton.setPadding(new Insets(10));
        // Create a new layout for the chat view
        BorderPane chatLayout = new BorderPane();
        theGuyIamChattingWith.setPadding(new Insets(5));
        theGuyIamChattingWith.setSpacing(50);
        me.setPadding(new Insets(10));
        me.setSpacing(90);
        chatline.getChildren().addAll(theGuyIamChattingWith, me);
        // Reuse the existing menu bar
        chatLayout.setTop(menuBar);

        // Create a TextArea for displaying chat messages
        chatDisplayArea.setEditable(false); // Make the chat area read-only
        chatDisplayArea.setWrapText(true); // Ensure text wraps within the TextArea

        // Create a ScrollPane to hold the chat display area
        chatScrollPane = new ScrollPane(chatline);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Create a TextField for typing messages
        HBox bottom = new HBox();
        bottom.getChildren().addAll(messageInputField, sendButton);
        HBox.setHgrow(messageInputField, Priority.ALWAYS);
        messageInputField.setPromptText("Type your message here...");
        messageInputField.setPadding(new Insets(10));
        ClientChat chat = client.getChat(selectedChat);
        client.setCurrentChat(chat);
        System.out.println("connected to: " + selectedChat);

        // Add the ScrollPane to the center of the BorderPane
        chatLayout.setCenter(chatScrollPane);

        // Add the TextField to the bottom of the BorderPane
        chatLayout.setBottom(bottom);

        // Create a new scene with the chat layout
        Scene chatScene = new Scene(chatLayout, scene.getWidth(), scene.getHeight());

        // Apply the scene to the primary stage
        primaryStage.setScene(chatScene);

        messageInputField.setOnAction(event -> {
            String message = messageInputField.getText();
            if (!message.trim().isEmpty()) {
                client.sendMessage(message);
                messageInputField.clear();
                chatDisplayArea.setScrollTop(Double.MAX_VALUE);
            }
        });

        sendButton.setOnAction(event -> {
            String message = messageInputField.getText();
            if (!message.trim().isEmpty()) {
                client.sendMessage(message);
                messageInputField.clear();
                chatDisplayArea.setScrollTop(Double.MAX_VALUE);
            }
        });



        // Ensure the prompt text stays when the field is empty
        messageInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                messageInputField.setPromptText("Type your message here...");
            }
        });

        // Set an action for the message input field to send a message when Enter is pressed


        System.out.println("Changed to chat view");
    }


    private void disconnect(){}

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
        System.out.println("Number of connections (ArrayList<ConnectionDetails> connections): " + connections.size());

        // Use an HBox for arranging connection boxes in a horizontal row
        HBox connectionsContainer = new HBox(10); // Horizontal gap between boxes
        connectionsContainer.setPadding(new Insets(10)); // Padding around the container

        // Bind the HBox's width to the ScrollPane's width
        connectionsContainer.prefWidthProperty().bind(scrollPane.widthProperty());

        double totalHeight = 0; // Variable to hold the total height of all connection boxes

        for (ConnectionDetails connection : connections) {
            VBox connectionBox = new VBox(5); // Individual connection box
            System.out.println("new connection: " + connection);

            // Set a fixed size for each connection box
            connectionBox.autosize(); // Preferred width and height

            // Prevent the VBox from being altered by setting min, max, and pref sizes to the same value
            connectionBox.setMinWidth(250);
            connectionBox.setPrefWidth(250);
            connectionBox.setMaxWidth(250);

            connectionBox.setPadding(new Insets(10)); // Padding inside each box
            connectionBox.setStyle("-fx-border-color: black;"); // Optional: adds a border around each box

            // Calculate the height of the current connection box
            double boxHeight = connectionBox.getPrefHeight();
            totalHeight += boxHeight; // Add the height to the total height

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
            connectionBox.getChildren().addAll(nameLabel ,hostLabel, portLabel, usernameLabel, passwordLabel);

            // Add the connection box to the HBox
            connectionsContainer.getChildren().add(connectionBox);
        }

        // Update the scroll pane content
        scrollPane.setContent(connectionsContainer);
        System.out.println("scrollpane got the connectionsContainer");

        // Dynamically set the minHeight of the ScrollPane to fit the content using a final variable
        double finalTotalHeight = totalHeight;
        scrollPane.minHeightProperty().bind(Bindings.createDoubleBinding(() -> finalTotalHeight, connectionsContainer.heightProperty()));

        scrollPane.requestLayout();
        System.out.println("requestlayout");
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
        Platform.runLater(() -> {
            Bubble bubble = new Bubble(message.getUsername() + " " + "\n" + "\n" + message.getContent() + "\n", String.valueOf(message.getPrettyTimestamp()));
            theGuyIamChattingWith.getChildren().addAll(bubble);
            // Automatisches Scrollen nach unten
            chatScrollPane.setVvalue(1.0);
        });
    }
    @Override
    public void displayOtherMessage(ClientChatMessage message) {
        Platform.runLater(() -> {
            Bubble bubble2 = new Bubble(message.getUsername() + " " + "\n" + "\n" + message.getContent() + "\n", String.valueOf(message.getPrettyTimestamp()));
            theGuyIamChattingWith.getChildren().addAll(bubble2);
            // Überprüfen, ob der Benutzer bereits am unteren Ende des Chats ist
            if (chatScrollPane.getVvalue() == 1.0) {
                // Wenn ja, automatisches Scrollen nach unten
                chatScrollPane.setVvalue(1.0);
            }
        });
    }

    @Override
    public void updateDisplay() {

    }
}