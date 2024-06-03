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
import javafx.scene.paint.Paint;
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
import net.jchad.client.view.Game2048;
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
    private Stage chatStage = new Stage();
    private Scene scene;
    private Stage dialogStage;
    private double sizeValue;
    private BorderPane borderPane = new BorderPane();
    private TextArea chatArea = new TextArea();
    private ConnectionDetailsBuilder connectionDetailsBuilder;
    private String selectedChat;
    private Menu connectionsMenu = new Menu("Connections");
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
    private Scene primaryScene;
    private Scene chatScene;
    private BorderPane chatLayout;
    MenuItem changeChat = new MenuItem("Change chat");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage; // Assign the primary stage to the class variable

        headerLabel.setText("Welcome Jchader!");
        contentLabel.setText("This is the best Chad-Platform out there");

        combinedLabel = new Label(headerLabel.getText() + "\n" + contentLabel.getText());


        MenuItem connect = new MenuItem("add connection");
        MenuItem disconnect = new MenuItem("remove connection");

        this.connectionsMenu.getItems().addAll(connect, disconnect);

        Menu settingsMenu = new Menu("Settings");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size (ctrl & +)");
        MenuItem standardFontSize = new MenuItem("standard Font size (crtl & R)");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size (crtl & -)");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);

        menuBar.getMenus().addAll(connectionsMenu, settingsMenu);

        double baseFontSize = screenWidth * 0.007;
        double windowWidth = screenWidth * 0.5;
        double windowHeight = screenHeight * 0.5;

        this.sizeValue = baseFontSize;

        borderPane.setTop(menuBar);
        borderPane.setBottom(scrollPane);
        borderPane.setCenter(combinedLabel);

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        connect.setOnAction(e -> connect()); // Pass null to indicate no pre-filled connection
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

        primaryScene = new Scene(borderPane, windowWidth, windowHeight);

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        primaryStage.setWidth(screenWidth / 1.5);
        primaryStage.setHeight(screenHeight / 1.5);

        primaryStage.setTitle("JChad Client");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        standardFontSizeMethod();
        displaySavedConnections();
    }

    // Modified connect() method to accept ConnectionDetails as a parameter
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
        Button addButton = new Button("Connect");
        Button saveButton = new Button("Add");

        addButton.setOnAction(e -> newConnection(hostField.getText(), portField.getText(), nameField.getText(), passwordField.getText(), usernameField.getText(), true));
        cancelButton.setOnAction(e -> {
            if (dialogStage!= null) {
                dialogStage.close();
            }
        });
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

        vbox.getChildren().addAll(grid, buttonContainer);

        Scene dialogScene = new Scene(vbox);
        dialogStage.setTitle("Connection Details");
        dialogStage.setScene(dialogScene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.sizeToScene();
        dialogStage.showAndWait();

    }

    public void connectSavedConnections(ConnectionDetails connection) {
        newConnection(connection.getHost(), String.valueOf(connection.getPort()), connection.getConnectionName(), connection.getPassword(), connection.getUsername(), true);
    }


    private void newConnection(String Host, String Port, String ConnectionName, String Password, String Username, Boolean connect) {
        Platform.runLater(() ->{
        if (Objects.equals(Host, "monke")) {
            try {
                vidPlayer.start(primaryStage); // Start the VidPlayer when the host is "monke"
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (dialogStage!= null) {
                    dialogStage.close();
                }
            return;
        }

            if (Objects.equals(Host, "2048")) {
                try {
                    Stage gameStage = new Stage();
                    new Game2048().start(gameStage);

                    if (dialogStage != null) {
                        dialogStage.close();
                    }
                    return;
                } catch (Exception e) {
                    System.out.println("error");
                }
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

        if (dialogStage!= null) {
            dialogStage.close();
        }
        });
    }

    public void changeToChat() {

        Button sendButton = new Button("Send");
        sendButton.setPadding(new Insets(10));
        // Create a new layout for the chat view
        chatLayout = new BorderPane();
        theGuyIamChattingWith.setPadding(new Insets(5));
        theGuyIamChattingWith.setSpacing(50);
        theGuyIamChattingWith.getChildren().clear();
        me.setPadding(new Insets(10));
        me.setSpacing(90);
        if(!chatline.getChildren().contains(theGuyIamChattingWith)) {
            chatline.getChildren().addAll(theGuyIamChattingWith);
        }
        // Reuse the existing menu bar
        chatLayout.setTop(menuBar);
        if(!connectionsMenu.getItems().contains(changeChat)) {
            connectionsMenu.getItems().add(changeChat);
        }
        changeChat.setOnAction(event -> {
            showChatSelectionWindow();
        });

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
        chatScene = new Scene(chatLayout, primaryScene.getWidth(), primaryScene.getHeight());

        // Apply the scene to the primary stage
        chatStage.setScene(chatScene);
        primaryStage.hide();
        chatStage.show();

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


    private void disconnect() {
        Platform.runLater(() -> {
            client.disconnect();

            // Erstellen einer neuen Instanz von GUI und Aufrufen der start Methode
            GUI guiInstance = new GUI();
            try {
                guiInstance.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            chatStage.hide();
            Platform.runLater(() -> primaryStage.show());
            standardFontSizeMethod();
            displaySavedConnections();
        });
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
        System.out.println("Number of connections (ArrayList<ConnectionDetails> connections): " + connections.size());

        // Use an HBox for arranging connection boxes in a horizontal row
        HBox connectionsContainer = new HBox(10); // Horizontal gap between boxes
        connectionsContainer.setPadding(new Insets(10, 10, 50, 10)); // Adjusted bottom padding to 50

        // Bind the HBox's width to the ScrollPane's width
        connectionsContainer.prefWidthProperty().bind(scrollPane.widthProperty());

        // Clear the previous content of the ScrollPane
        scrollPane.setContent(null);

        double totalHeight = 0; // Variable to hold the total height of all connection boxes

        for (ConnectionDetails connection : connections) {
            VBox connectionBox = createConnectionBox(connection); // Assuming createConnectionBox is defined elsewhere

            // Calculate the height of the current connection box
            double boxHeight = connectionBox.getPrefHeight();
            totalHeight += boxHeight;

            // Add the connection box to the HBox
            connectionsContainer.getChildren().add(connectionBox);
        }

        // Update the scroll pane content
        scrollPane.setContent(connectionsContainer);

        // Dynamically set the minHeight of the ScrollPane to fit the content using a binding
        double finalTotalHeight = totalHeight;
        scrollPane.minHeightProperty().bind(Bindings.createDoubleBinding(() -> finalTotalHeight, connectionsContainer.heightProperty()));

        scrollPane.requestLayout();
        System.out.println("requestlayout");
    }

    private VBox createConnectionBox(ConnectionDetails connection) {
        VBox connectionBox = new VBox(5); // Individual connection box
        System.out.println("new connection: " + connection);

        // Set a fixed size for each connection box
        connectionBox.autosize(); // Preferred width and height

        // Prevent the VBox from being altered by setting min, max, and pref sizes to the same value
        connectionBox.setMinWidth(300);
        connectionBox.setPrefWidth(300);
        connectionBox.setMaxWidth(300);

        // Increase the padding to add more space at the top and bottom
        connectionBox.setPadding(new Insets(10, 10, 10, 10)); // Top, Right, Bottom, Left

        connectionBox.setStyle("-fx-border-color: black;"); // Optional: adds a border around each box

        // Create labels for each piece of connection information
        Label hostLabel = new Label("Host: " + connection.getHost());
        Label portLabel = new Label("Port: " + connection.getPort());
        Label nameLabel = new Label("Name: " + connection.getConnectionName());
        Label usernameLabel = new Label("Username: " + connection.getUsername());
        Label passwordLabel = new Label("Password: " + "*".repeat(connection.getPassword().length()));

        // Add text alignment for better visual appearance
        hostLabel.setTextAlignment(TextAlignment.LEFT);
        portLabel.setTextAlignment(TextAlignment.LEFT);
        nameLabel.setTextAlignment(TextAlignment.LEFT);
        usernameLabel.setTextAlignment(TextAlignment.LEFT);
        passwordLabel.setTextAlignment(TextAlignment.LEFT);

        // Add labels to the connection box
        connectionBox.getChildren().addAll(nameLabel, hostLabel, portLabel, usernameLabel, passwordLabel);

        // Add mouse click event listener to the connection box
        connectionBox.setOnMouseClicked(event -> savedConnectionWindow(connection));

        return connectionBox;
    }

    private void savedConnectionWindow(ConnectionDetails connection) {
        // Create a new stage for the saved connection options window
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Saved Connection Options");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Replicate the structure and styling of the connection box
        VBox connectionInfoBox = new VBox(5);
        connectionInfoBox.setMinWidth(250);
        connectionInfoBox.setPrefWidth(250);
        connectionInfoBox.setMaxWidth(250);
        connectionInfoBox.setPadding(new Insets(10));
        connectionInfoBox.setStyle("-fx-border-color: black;");

        Label hostLabel = new Label("Host: " + connection.getHost());
        Label portLabel = new Label("Port: " + connection.getPort());
        Label nameLabel = new Label("Name: " + connection.getConnectionName());
        Label usernameLabel = new Label("Username: " + connection.getUsername());
        Label passwordLabel = new Label("Password: " + "*".repeat(connection.getPassword().length()));

        hostLabel.setTextAlignment(TextAlignment.LEFT);
        portLabel.setTextAlignment(TextAlignment.LEFT);
        nameLabel.setTextAlignment(TextAlignment.LEFT);
        usernameLabel.setTextAlignment(TextAlignment.LEFT);
        passwordLabel.setTextAlignment(TextAlignment.LEFT);

        connectionInfoBox.getChildren().addAll(nameLabel, hostLabel, portLabel, usernameLabel, passwordLabel);

        Button deleteButton = new Button("Delete");
        Button useButton = new Button("Use");
        deleteButton.setOnAction(e -> {
            // Remove the connection from the client configuration
            client.configuration().removeConnection(connection);
            // Refresh the display of saved connections
            displaySavedConnections();
            dialogStage.close(); // Close the dialog stage after deleting the connection
            handleInfo("Saved Connection got deleted!");
        });

        useButton.setOnAction(e -> {
            connectSavedConnections(connection);
            dialogStage.close(); // Close the dialog stage after using the connection
        });

        HBox buttonContainer = new HBox(useButton, deleteButton);
        buttonContainer.setSpacing(10); // Adjust spacing as needed

        vbox.getChildren().addAll(connectionInfoBox, buttonContainer);

        Scene dialogScene = new Scene(vbox);
        dialogStage.setScene(dialogScene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.showAndWait();
    }

    @Override
    public void handleFatalError(Exception e) {
        new ClientAlerts(Alert.AlertType.ERROR, "Fatal Error", e.toString());
        e.printStackTrace();
        // TODO printstackTrace löschen nach fixen
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
            Bubble bubble = new Bubble(message.getUsername() + " " + "\n" + "\n" + message.getContent() + "\n", String.valueOf(message.getPrettyTimestamp()), chatScrollPane.getWidth() / 3.5);
            bubble.setBubbleColor(Color.GREEN);
            theGuyIamChattingWith.getChildren().addAll(bubble);
            // Automatischesdie connection closed  Scrollen nach unten
            chatScrollPane.setVvalue(1.0);
        });
    }
    @Override
    public void displayOtherMessage(ClientChatMessage message) {
        Platform.runLater(() -> {
            Bubble bubble2 = new Bubble(message.getUsername() + " " + "\n" + "\n" + message.getContent() + "\n", String.valueOf(message.getPrettyTimestamp()), chatScrollPane.getWidth() / 3.5);
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