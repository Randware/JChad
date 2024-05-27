package net.jchad.client.view.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
    private Stage primaryStage; // Store the primary stage to access it later
    private Scene scene;
    private ConnectionDetailsBuilder connectionDetailsBuilder;
    String selectedChat;

    VBox messageBox = new VBox();
    ClientChat chat;

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
        displayPrompt("Test","Test");
    }

    public void connect() {
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

        Button addButton = new Button("Add");

        addButton.setOnAction(e -> newConnection(hostField.getText(), portField.getText(), nameField.getText(), passwordField.getText(), usernameField.getText()));
        cancelButton.setOnAction(e -> dialogStage.close());

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

    private void newConnection(String Host, String Port, String ConnectionName, String Password, String Username) {
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
        client.connect(connectionDetailsBuilder.build());
        changeToChat();
        handleInfo("Successfully connected to: " + ConnectionName);
    }

    private void changeToChat() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(messageBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter message...");
        HBox inputBox = new HBox(inputField);
        HBox.setHgrow(inputField, Priority.ALWAYS);


        BorderPane chatLayout = new BorderPane();
        chatLayout.setBottom(inputBox);
        chatLayout.setCenter(scrollPane);
        chatLayout.setTop(menuBar);

        Scene chatScene = new Scene(chatLayout, scene.getWidth(), scene.getHeight());
        primaryStage.setScene(chatScene);

        showChatSelectionWindow();
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
        Platform.runLater(() -> {
            selectButton.setOnAction(event -> {
                selectedChat = chatListView.getSelectionModel().getSelectedItem();
                if (selectedChat!= null) {
                    // Handle the selected chat here
                    System.out.println("Selected Chat: " + selectedChat);

                    chat = client.getChat(selectedChat);
                    client.setCurrentChat(chat);
                    System.out.println(client.getChatMessages(chat));
                    dialogStage.close();
                }
            });
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


    @Override
    public void handleFatalError(Exception e) {
        Platform.runLater(() -> new ClientAlerts(Alert.AlertType.ERROR, "Fatal Error", e.toString()));
    }

    @Override
    public void handleError(Exception e) {
        Platform.runLater(() -> new ClientAlerts(Alert.AlertType.ERROR, "Error", e.toString()));
    }

    @Override
    public void handleWarning(String warning) {
        Platform.runLater(() -> new ClientAlerts(Alert.AlertType.WARNING, "Warning", warning));
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
        Text textMessage = new Text(message.getUsername() + " " + message.getPrettyTimestamp() + ": " + message.getContent());
        messageBox.getChildren().add(textMessage);
    }

    @Override
    public void updateDisplay() {

    }
}
