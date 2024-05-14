package net.jchad.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jchad.server.controller.ServerController;
import net.jchad.shared.common.Util;
import net.jchad.server.model.error.MessageHandler;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import org.fxmisc.richtext.InlineCssTextArea;

public class GUI extends Application implements MessageHandler {
    private ServerController server;
    private final InlineCssTextArea logArea = new InlineCssTextArea();
    private final TextField cmdField = new TextField();
    private double sizeValue;
    private final ScrollPane scrollPane = new ScrollPane();
    private final KeyCombination crtlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    private final MenuBar menuBar = new MenuBar();
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();

    // Launch method
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        server = new ServerController(this);
        server.startServer();

        cmdField.setPromptText("Enter command here...");

        menuBar.setPadding(Insets.EMPTY);
        logArea.setEditable(false);

        Menu settingsMenu = new Menu("Settings");
        Menu serverMenu = new Menu("Server");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size (ctrl & +)");
        MenuItem standardFontSize = new MenuItem("standard Font size (crtl & R)");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size (crtl & -)");
        MenuItem stopServerItem = new MenuItem("Stop server");
        MenuItem startServerItem = new MenuItem("Start server");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);
        serverMenu.getItems().addAll(stopServerItem, startServerItem);

        menuBar.getMenus().addAll(settingsMenu, serverMenu);

        scrollPane.setContent(logArea);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox vbox = new VBox(menuBar, scrollPane, cmdField);

        double baseFontSize = screenWidth * 0.007;
        double windowWidth = screenWidth * 0.5;
        double windowHeight = screenHeight * 0.5;

        this.sizeValue = baseFontSize;

        increaseFontSize.setOnAction(e -> changeFontSize(2));
        standardFontSize.setOnAction(e -> standardFontSizeMethod());
        decreaseFontSize.setOnAction(e -> changeFontSize(-2));
        stopServerItem.setOnAction(e -> server.stopServer());
        startServerItem.setOnAction(e -> server.startServer());

        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        VBox.setMargin(menuBar, new Insets(0, -10, 0, -10));
        VBox.setVgrow(logArea, Priority.ALWAYS);

        vbox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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

        cmdField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (!cmdField.getText().isEmpty()) {
                    server.sendCommand(cmdField.getText());
                    appendText(" > " + cmdField.getText() + "\n", "-fx-fill: black;");
                    cmdField.setText("");
                }
            }
        });

        Scene scene = new Scene(vbox, windowWidth, windowHeight);
        stage.setTitle("Server GUI");
        stage.setScene(scene);
        stage.show();

        changeFontSize((int) baseFontSize);

        scrollPane.prefWidthProperty().bind(scene.widthProperty());
        scrollPane.prefHeightProperty().bind(scene.heightProperty().subtract(menuBar.getHeight() + cmdField.getHeight() + 20));
        logArea.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));  // Subtract for padding/scrollbar
        logArea.setWrapText(true);
        standardFontSizeMethod();
    }

    private void changeFontSize(int size) {
        this.sizeValue = sizeValue + size;
        if (sizeValue < 0) {
            this.sizeValue = 1;
        } else if (sizeValue > 50) {
            this.sizeValue = 50;
        }

        logArea.setStyle("-fx-font-size: " + sizeValue);
        cmdField.setStyle("-fx-font-size: " + sizeValue);
        menuBar.setStyle("-fx-font-size: " + sizeValue);
    }

    private void standardFontSizeMethod() {
        this.sizeValue = screenWidth * 0.01;

        logArea.setStyle("-fx-font-size: " + sizeValue);
        cmdField.setStyle("-fx-font-size: " + sizeValue);
        menuBar.setStyle("-fx-font-size: " + sizeValue);
    }

    private void appendText(String text, String style) {
        Platform.runLater(() -> {
            int length = logArea.getLength();
            logArea.appendText(text);
            logArea.setStyle(length, length + text.length(), style);
            logArea.requestFollowCaret();
        });
    }

    @Override
    public void handleFatalError(Exception e) {
        appendText("  ☠ [Fatal Error]: ", "-fx-fill: #fd0000; -fx-font-weight:bold;");
        appendText(Util.stackTraceToString(e) + "\n", "-fx-fill: #000000; -fx-font-weight:normal;");
    }

    @Override
    public void handleError(Exception e) {
        appendText("  Ⓧ [Error]: ", "-fx-fill: #fd6e00; -fx-font-weight:bold;");
        appendText(e.getMessage() + "\n", "-fx-fill: #000000; -fx-font-weight:normal;");
    }

    @Override
    public void handleWarning(String warning) {
        appendText("  ⚠ [Warning]: ", "-fx-fill: #d2c800; -fx-font-weight:bold;");
        appendText(warning + "\n", "-fx-fill: #000000; -fx-font-weight:normal;");
    }

    @Override
    public void handleInfo(String info) {
        appendText("  ⓘ [Info]: ", "-fx-fill: #00bafd; -fx-font-weight:bold;");
        appendText(info + "\n", "-fx-fill: #000000; -fx-font-weight:normal;");
    }

    @Override
    public void handleDebug(String debug) {
        if (!server.getServerConfig().getServerSettings().isDebugMode()) return;
        appendText("  ⚙ [Debug]: ", "-fx-fill: #626262; -fx-font-weight:bold;");
        appendText(debug + "\n", "-fx-fill: #000000; -fx-font-weight:normal;");
    }

    @Override
    public void stop() {
        if (server.isRunning()) {
            server.stopServer();
        }
        System.exit(0);
    }
}
