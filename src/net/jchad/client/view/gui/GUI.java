package net.jchad.client.view.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.jchad.client.controller.ClientController;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChat;
import net.jchad.client.model.store.chat.ClientChatMessage;
import net.jchad.client.model.store.connection.ConnectionDetails;
import net.jchad.client.model.store.connection.ConnectionDetailsBuilder;
import net.jchad.client.view.Game2048;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GUI extends Application implements ViewCallback {
    private ClientController client = new ClientController(this);
    private MenuBar menuBar = new MenuBar();
    private ScrollPane scrollPane = new ScrollPane();
    private Label headerLabel = new Label();
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
    private DropShadow dropShadow = new DropShadow();
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

        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(0.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5)); // semi-transparent black


        animateText(headerLabel);

        MenuItem connect = new MenuItem("New connection");
        MenuItem disconnect = new MenuItem("Disconnect");

        this.connectionsMenu.getItems().addAll(connect, disconnect);

        Menu settingsMenu = new Menu("Settings");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("Increase Font size (ctrl & +)");
        MenuItem standardFontSize = new MenuItem("Standard Font size (crtl & R)");
        MenuItem decreaseFontSize = new MenuItem("Decrease Font size (crtl & -)");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);

        menuBar.getMenus().addAll(connectionsMenu, settingsMenu);

        double baseFontSize = screenWidth * 0.007;
        double windowWidth = screenWidth * 0.5;
        double windowHeight = screenHeight * 0.5;

        this.sizeValue = baseFontSize;

        borderPane.setTop(menuBar);
        borderPane.setBottom(scrollPane);
        borderPane.setCenter(headerLabel);

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


    public static void animateText(Label label) {
        // Set the font to bold
        label.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Define the base text
        String baseText = "Welcome to ";
        String initialText = "JChad!";

        // Define the phrases to replace "JChad!" with
        String[] phrases = {
                "freedom",
                "your secure messenger",
                "the ultimate chat platform",
                "Free and Open-Source Software",
                "privacy by design",
                "JChad <3",
                "the platform for chads",
                "anonymity",
                "a fresh chat experience",
                "the chat platform of your choice",
                "everything you need, nothing you don't"
        };

        // Add a caret to the end of the text
        final String caret = "|";
        final String blankSpace = " ";
        label.setText(baseText + initialText + caret);

        // Create a Timeline for the animation
        Timeline timeline = new Timeline();

        // Variable to keep track of the current character index
        final int[] charIndex = {0};

        // Duration for each keyframe
        Duration frameDuration = Duration.millis(150); // Adjusted for slower and smoother animation

        // Create a random number generator
        Random random = new Random();

        // Function to add keyframes for the typing animation of a phrase
        java.util.function.Consumer<String> addTypingAnimation = phrase -> {
            // Add the new phrase one character at a time
            for (int i = 0; i < phrase.length(); i++) {
                final String newText = baseText + phrase.substring(0, i + 1);
                timeline.getKeyFrames().add(new KeyFrame(frameDuration.multiply(charIndex[0]++), e -> {
                    label.setText(newText + caret);
                }));
            }

            // Random pause duration with slower blinking caret
            int randomPause = 10 + random.nextInt(10);
            for (int i = 0; i < randomPause; i++) {
                final int pauseIndex = i;
                timeline.getKeyFrames().add(new KeyFrame(frameDuration.multiply(charIndex[0]++), e -> {
                    label.setText(baseText + phrase + ((pauseIndex % 4 < 2) ? caret : blankSpace));
                }));
            }

            // Remove the phrase character by character
            for (int i = phrase.length(); i >= 0; i--) {
                final String remainingText = baseText + phrase.substring(0, i);
                timeline.getKeyFrames().add(new KeyFrame(frameDuration.multiply(charIndex[0]++), e -> {
                    label.setText(remainingText + caret);
                }));
            }
        };

        // Initial display of "JChad!"
        for (int i = initialText.length(); i >= 0; i--) {
            final int currentLength = initialText.length() - i;
            timeline.getKeyFrames().add(new KeyFrame(frameDuration.multiply(charIndex[0]++), e -> {
                String currentText = baseText + initialText.substring(0, currentLength);
                label.setText(currentText + caret);
            }));
        }

        // Random pause duration with slower blinking caret
        int randomPause = 12 + random.nextInt(10);
        for (int i = 0; i < randomPause; i++) {
            final int pauseIndex = i;
            timeline.getKeyFrames().add(new KeyFrame(frameDuration.multiply(charIndex[0]++), e -> {
                label.setText(baseText + initialText + ((pauseIndex % 4 < 2) ? caret : blankSpace));
            }));
        }

        for (int i = initialText.length(); i >= 0; i--) {
            final String remainingText = baseText + initialText.substring(0, i);
            timeline.getKeyFrames().add(new KeyFrame(frameDuration.multiply(charIndex[0]++), e -> {
                label.setText(remainingText + caret);
            }));
        }


        // Add animations for each phrase in random order
        for (int i = 0; i < phrases.length; i++) {
            String randomPhrase = phrases[random.nextInt(phrases.length)];
            addTypingAnimation.accept(randomPhrase);
        }

        // Start the animation
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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

        cancelButton.setStyle("-fx-background-color: #ff5555; -fx-text-fill: #FFFFFF;");
        cancelButton.setEffect(dropShadow);
        addButton.setStyle("-fx-background-color: #48ac3a; -fx-text-fill: #FFFFFF;");
        addButton.setEffect(dropShadow);
        saveButton.setStyle("-fx-background-color: #3a87b6; -fx-text-fill: #FFFFFF;");
        saveButton.setEffect(dropShadow);

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
            if (Objects.equals(Host, "2048")) {
                try {
                    Stage gameStage = new Stage();
                    new Game2048().start(gameStage);

                    if (dialogStage != null) {
                        dialogStage.close();
                    }
                    return;
                } catch (Exception e) {}
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
            client.connect(connectionDetailsBuilder.build());
            showChatSelectionWindow();
            if (selectedChat != null) {
                handleInfo("Successfully connected to: " + selectedChat);
            } else {
                //User pressed cancel
            }
        }else {
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

        theGuyIamChattingWith.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0); // Scroll to the bottom whenever content height changes
        });

        // Add the ScrollPane to the center of the BorderPane
        chatLayout.setCenter(chatScrollPane);

        // Add the TextField to the bottom of the BorderPane
        chatLayout.setBottom(bottom);

        // Create a new scene with the chat layout
        chatScene = new Scene(chatLayout, primaryScene.getWidth(), primaryScene.getHeight());

        chatStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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
        String cssStyle = "-fx-font-size: " + sizeValue + ";";

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
        cancelButton.setStyle("-fx-background-color: #ff5555; -fx-text-fill: #FFFFFF;");
        cancelButton.setEffect(dropShadow);
        selectButton.setStyle("-fx-background-color: #3a87b6; -fx-text-fill: #FFFFFF;");
        selectButton.setEffect(dropShadow);

        // Action for Select button
        selectButton.setOnAction(event -> {
            selectedChat = chatListView.getSelectionModel().getSelectedItem();
            if (selectedChat!= null) {
                // Handle the selected chat here
                changeToChat();
                dialogStage.close();
            }
        });



        // Action for Cancel button
        cancelButton.setOnAction(event -> disconnect());

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

        chatListView.setStyle(cssStyle);
        selectButton.setStyle(cssStyle);
        cancelButton.setStyle(cssStyle);
        buttonContainer.setStyle(cssStyle);
    }



    private void standardFontSizeMethod() {
        this.sizeValue = screenWidth * 0.01;
        updateWindowFontSizeEverything();
    }

    private void changeFontSize(int size) {
        this.sizeValue = sizeValue + size;
        if (sizeValue < 0) {
            this.sizeValue = 1;
        } else if (sizeValue > 50) {
            this.sizeValue = 50;
        }
        updateWindowFontSizeEverything();
    }

    public void updateWindowFontSizeEverything() {
        // Define a CSS string with the new font size
        String cssStyle = "-fx-font-size: " + sizeValue + ";";

        // Apply the font size to the main application components
        menuBar.setStyle(cssStyle);
        headerLabel.setStyle(cssStyle);
        scrollPane.setStyle(cssStyle);

        // Apply the font size to the chat view components
        chatDisplayArea.setStyle(cssStyle);
        messageInputField.setStyle(cssStyle);

        if (dialogStage!= null && dialogStage.isShowing()) {
            for (Node node : dialogStage.getScene().getRoot().lookupAll(".text")) {
                node.setStyle(cssStyle);
            }
        }

        //updates the fontsize for the saved connections
        HBox hbox = (HBox) scrollPane.getContent();
        if (hbox != null) {
            for (Node node : hbox.getChildren()) {
                ((VBox) node).getChildren().forEach((node1 -> {
                    try {
                        Label label = (Label) node1;
                        label.setFont(new Font(label.getFont().getName(), sizeValue));
                    } catch (ClassCastException e) {
                        //If the element is not a label.
                        //This just gets ignored
                    }
                }));
            }
        }
    }

    private void displaySavedConnections() {
        ArrayList<ConnectionDetails> connections = client.configuration().getConnections();

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
    }




    private VBox createConnectionBox(ConnectionDetails connection) {
        VBox connectionBox = new VBox(10); // Increased spacing between elements

        // Set fixed size for each connection box
        connectionBox.setMinWidth(300);
        connectionBox.setPrefWidth(300);
        connectionBox.setMaxWidth(300);

        // Add padding inside the box
        connectionBox.setPadding(new Insets(20)); // Increased padding for better spacing

        // Create a solid background color
        Color backgroundColor = Color.CORNFLOWERBLUE;
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, new CornerRadii(10), Insets.EMPTY);
        connectionBox.setBackground(new Background(backgroundFill));

        // Set border with rounded corners
        connectionBox.setStyle("-fx-border-color: #4A90E2; -fx-border-radius: 10; -fx-border-width: 2px;");

        // Add a drop shadow effect in the middle
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.GRAY);
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.3);
        connectionBox.setEffect(dropShadow);

        // Create labels for each piece of connection information
        Label nameLabel = new Label(connection.getConnectionName());
        Label hostLabel = new Label("Host: " + connection.getHost());
        Label portLabel = new Label("Port: " + connection.getPort());
        Label usernameLabel = new Label("Username: " + connection.getUsername());
        Label passwordLabel = new Label("Password: " + "*".repeat(connection.getPassword().length()));

        // Customize label fonts and text alignment
        Font titleFont = Font.font("Arial", FontWeight.BOLD, 18);
        Font font = new Font("Arial", 14);

        nameLabel.setFont(titleFont);
        hostLabel.setFont(font);
        portLabel.setFont(font);
        usernameLabel.setFont(font);
        passwordLabel.setFont(font);

        nameLabel.setTextAlignment(TextAlignment.LEFT);
        hostLabel.setTextAlignment(TextAlignment.LEFT);
        portLabel.setTextAlignment(TextAlignment.LEFT);
        usernameLabel.setTextAlignment(TextAlignment.LEFT);
        passwordLabel.setTextAlignment(TextAlignment.LEFT);

        nameLabel.setTextFill(Color.WHITE);
        hostLabel.setTextFill(Color.WHITE);
        portLabel.setTextFill(Color.WHITE);
        usernameLabel.setTextFill(Color.WHITE);
        passwordLabel.setTextFill(Color.WHITE);

        // Add a line under the connection name
        Line line = new Line();
        line.setStartX(0);
        line.setEndX(260); // Adjust to fit the width of the box
        line.setStroke(Color.WHITE);

        // Make the password label clickable to reveal/hide the password
        passwordLabel.setOnMouseClicked(event -> {
            if (passwordLabel.getText().contains("*")) {
                passwordLabel.setText("Password: " + connection.getPassword());
            } else {
                passwordLabel.setText("Password: " + "*".repeat(connection.getPassword().length()));
            }
            event.consume(); // Prevents triggering the VBox click event
        });
        passwordLabel.setStyle("-fx-cursor: hand;");

        // Add labels and line to the connection box
        connectionBox.getChildren().addAll(nameLabel, line, hostLabel, portLabel, usernameLabel, passwordLabel);

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
        deleteButton.setStyle("-fx-background-color: #ff5555; -fx-text-fill: #FFFFFF;");
        deleteButton.setEffect(dropShadow);
        Button useButton = new Button("Connect");
        useButton.setStyle("-fx-background-color: #48ac3a; -fx-text-fill: #FFFFFF;");
        useButton.setEffect(dropShadow);
        deleteButton.setOnAction(e -> {
            // Remove the connection from the client configuration
            client.configuration().removeConnection(connection);
            // Refresh the display of saved connections
            displaySavedConnections();
            handleInfo("Saved Connection got deleted!");
            dialogStage.close(); // Close the dialog stage after deleting the connection

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
        Platform.runLater(() -> {
            if (e instanceof ExecutionException) {
                new ClientAlerts(Alert.AlertType.ERROR, "Fatal Error", e.getCause().getMessage());
            } else {
                new ClientAlerts(Alert.AlertType.ERROR, "Fatal Error", e.toString());
            }
        });

        disconnect();
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
     * <p><b><u><font color="red">THIS METHODE DOES NOT WORK!</font></u></b></p>
     * <p>I am unable to fix this because of <b>JavaFX</b></p>
     * <p><u>The problem</u>:</p>
     * <ul>
     *     <li>The {@link net.jchad.client.model.client.connection.ServerConnector ServerConnector}
     *     access this methode from its own thread. This is huge problem because the application behaves
     *     really strange since I have to use {@code Platform.runLater{() -> ()}}.
     *     This messes up the entire order and this methode just returns an empty string. (I think)
     *     </li>
     *
     *     <li>
     *         {@code Platform.runLater{() -> ()}} this also somehow "destroys" the {@code stage.showAndWait()}
     *         methode because it seems that it does not wait and immediately continues the code execution.
     *     </li>
     *
     * </ul>
     *
     * <p>This <font color="red">issue</font> <u>needs further investigations</u> to exactly know the problem
     *    and to find a solution</p>
     *
     * @param promptTitle the title of the displayed prompt.
     * @param promptMessage the full message of the prompt displayed.
     * @return The new value (set by the user)
     */
    @Override
    public String displayPrompt(String promptTitle, String promptMessage) {

            //This code prevents exception. This has to be removed if you are trying to fix the code
            boolean disableCode = true;
            if (disableCode) {
                return "";
            }


            StringBuilder string = new StringBuilder();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Platform.runLater(() -> {

                InputPrompt inputPrompt = new InputPrompt(Alert.AlertType.CONFIRMATION, promptTitle, promptMessage);
                // Show the input prompt and wait for user input
               inputPrompt.showAndWait();
                String input = inputPrompt.getInput();



                if (input != null && !input.isEmpty()) {
                    string.replace(0, string.length(), "");
                    string.append(input);
                } else {
                    // disconnect the currently running connection or connection process,
                    // since the user didn't provide the required information.
                    client.disconnect();
                    return;
                }

                // Countdown the latch to unblock the calling thread
            });


            return string.toString();

    }

    @Override
    public void displayOwnMessage(ClientChatMessage message) {
        Platform.runLater(() -> {
            Bubble bubble = new Bubble(message.getUsername() + " " + "\n" + "\n" + message.getContent() + "\n", message.getPrettyTimestamp(), chatScrollPane.getWidth() / 3.5);
            bubble.setBubbleColor(Color.GREEN);
            theGuyIamChattingWith.getChildren().addAll(bubble);
            // Automatischesdie connection closed  Scrollen nach unten
            chatScrollPane.setVvalue(1.0);
        });
    }
    @Override
    public void displayOtherMessage(ClientChatMessage message) {
        chatScrollPane.setVvalue(1.0);
        Platform.runLater(() -> {
            Bubble bubble2 = new Bubble(message.getUsername() + " " + "\n" + "\n" + message.getContent() + "\n", message.getPrettyTimestamp(), chatScrollPane.getWidth() / 3.5);
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