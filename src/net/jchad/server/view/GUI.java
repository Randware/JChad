package net.jchad.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jchad.server.controller.ServerController;
import net.jchad.shared.common.Util;
import net.jchad.server.model.error.MessageHandler;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

// Responsible for displaying server output in GUI mode
public class GUI extends Application implements MessageHandler {
    private ServerController server;
    private TextFlow logArea = new TextFlow();
    private TextField cmdField = new TextField();
    private double sizeValue;// = 13;
    private ScrollPane scrollPane = new ScrollPane();
    private final KeyCombination crtlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    private MenuBar menuBar = new MenuBar();
    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    double screenWidth = screenBounds.getWidth();
    double screenHeight = screenBounds.getHeight();

    //launch method
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        server = new ServerController(this);
        server.startServer();

        cmdField.setPromptText("Enter command here...");

        menuBar.setPadding(Insets.EMPTY);

        Menu settingsMenu = new Menu("Settings");
        Menu stopServerMenu = new Menu("Stop Server");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size (ctrl & +)");
        MenuItem standardFontSize = new MenuItem("standard Font size (crtl & R)");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size (crtl & -)");
        MenuItem stopServerItem = new MenuItem("Stop Server");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);
        stopServerMenu.getItems().add(stopServerItem);

        menuBar.getMenus().addAll(settingsMenu,stopServerMenu);

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

        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 10, 10));
        VBox.setVgrow(logArea, Priority.ALWAYS);
        //vbox.setMaxHeight(Double.MAX_VALUE);
        //vbox.setStyle("-fx-font-size: 13px;");

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

        /*logArea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                // Logik zum Extrahieren und Kopieren des Textes von t2
            }
        });*/

        cmdField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (cmdField.getText().length() > 0) {
                    server.sendCommand(cmdField.getText());
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

    @Override
    public void handleFatalError(Exception e) {
        Platform.runLater(() -> {
            String log = "  Ⓧ [Fatal Error]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #fd0000;-fx-font-weight:bold;");
            Text t2 = new Text(Util.stackTraceToString(e)+ "\n");
            t2.setStyle("-fx-font-weight:normal;");

            t2.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem copyMenuItem = new MenuItem("Kopieren");
                    contextMenu.getItems().add(copyMenuItem);

                    copyMenuItem.setOnAction(copyEvent -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(t2.getText());
                        clipboard.setContent(content);
                    });

                    contextMenu.show(t2, event.getScreenX(), event.getScreenY());
                }
            });

            logArea.getChildren().addAll(t1, t2);
            logArea.getChildren().addListener((ListChangeListener<Node>) change -> {
                logArea.layout(); // Aktualisieren Sie das Layout des TextFlow
                scrollPane.layout(); // Aktualisieren Sie das Layout des ScrollPane
                scrollPane.setVvalue(Double.MAX_VALUE); // Scrollen Sie nach unten
                changeFontSize(0);
            });
            changeFontSize(0);
        });

        server.stopServer();
    }

    @Override
    public void handleError(Exception e) {
        Platform.runLater(() -> {
            String log = "  Ⓧ [Error]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #fd6e00;-fx-font-weight:bold;");
            Text t2 = new Text(e.getMessage() + "\n");
            t2.setStyle("-fx-font-weight:normal;");

            t2.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem copyMenuItem = new MenuItem("Kopieren");
                    contextMenu.getItems().add(copyMenuItem);

                    copyMenuItem.setOnAction(copyEvent -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(t2.getText());
                        clipboard.setContent(content);
                    });

                    contextMenu.show(t2, event.getScreenX(), event.getScreenY());
                }
            });

            logArea.getChildren().addAll(t1, t2);
            logArea.getChildren().addListener((ListChangeListener<Node>) change -> {
                logArea.layout(); // Aktualisieren Sie das Layout des TextFlow
                scrollPane.layout(); // Aktualisieren Sie das Layout des ScrollPane
                scrollPane.setVvalue(Double.MAX_VALUE); // Scrollen Sie nach unten
                changeFontSize(0);
            });
            changeFontSize(0);
        });
    }

    @Override
    public void handleWarning(String warning) {
        Platform.runLater(() -> {
            String log = "  ⚠ [Warning]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #d2c800;-fx-font-weight:bold;");
            Text t2 = new Text(warning + "\n");
            t2.setStyle("-fx-font-weight:normal;");

            t2.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem copyMenuItem = new MenuItem("Kopieren");
                    contextMenu.getItems().add(copyMenuItem);

                    copyMenuItem.setOnAction(copyEvent -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(t2.getText());
                        clipboard.setContent(content);
                    });

                    contextMenu.show(t2, event.getScreenX(), event.getScreenY());
                }
            });

            logArea.getChildren().addAll(t1, t2);
            logArea.layout(); // Aktualisieren Sie das Layout des TextFlow
            logArea.getChildren().addListener((ListChangeListener<Node>) change -> {
                logArea.layout(); // Aktualisieren Sie das Layout des TextFlow
                scrollPane.layout(); // Aktualisieren Sie das Layout des ScrollPane
                scrollPane.setVvalue(Double.MAX_VALUE); // Scrollen Sie nach unten
                changeFontSize(0);
            });
        });
    }

    @Override
    public void handleInfo(String info) {
        Platform.runLater(() -> {
            String log = "  ⓘ [Info]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #00bafd;-fx-font-weight:bold;");
            Text t2 = new Text(info + "\n");
            t2.setStyle("-fx-font-weight:normal;");

            t2.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem copyMenuItem = new MenuItem("Kopieren");
                    contextMenu.getItems().add(copyMenuItem);

                    copyMenuItem.setOnAction(copyEvent -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(t2.getText());
                        clipboard.setContent(content);
                    });

                    contextMenu.show(t2, event.getScreenX(), event.getScreenY());
                }
            });

            logArea.getChildren().addAll(t1, t2);
            logArea.getChildren().addListener((ListChangeListener<Node>) change -> {
                logArea.layout(); // Aktualisieren Sie das Layout des TextFlow
                scrollPane.layout(); // Aktualisieren Sie das Layout des ScrollPane
                scrollPane.setVvalue(Double.MAX_VALUE); // Scrollen Sie nach unten
                changeFontSize(0);
            });
        });

    }
}
