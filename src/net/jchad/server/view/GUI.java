package net.jchad.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import net.jchad.server.controller.ServerController;
import net.jchad.server.model.error.MessageHandler;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
//TODO: 1. Zeilenumbruch bei logArea finished
//TODO: 2. Copy pasta bei logArea
//TODO: 3. Menubar font size change finished
//TODO: 4. Font size beim ersten öffnen an bildschirm anpassen

// Responsible for displaying server output in GUI mode
public class GUI extends Application implements MessageHandler {
    private ServerController server;
    private TextFlow logArea = new TextFlow();
    private TextField cmdField = new TextField();
    private double sizeValue = 13;
    private ScrollPane scrollPane = new ScrollPane();
    private final KeyCombination crtlMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlPlus = new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN);
    private final KeyCombination crtlR = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
    private MenuBar menuBar = new MenuBar();
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

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size (ctrl & +)");
        MenuItem standardFontSize = new MenuItem("standard Font size (crtl & R)");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size (crtl & -)");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);

        menuBar.getMenus().add(settingsMenu);

        scrollPane.setContent(logArea);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox vbox = new VBox(menuBar, scrollPane, cmdField);

        increaseFontSize.setOnAction(e -> changeFontSize(2));
        standardFontSize.setOnAction(e -> standardFontSizeMethod());
        decreaseFontSize.setOnAction(e -> changeFontSize(-2));

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

        DoubleProperty fontSize = new SimpleDoubleProperty(13);

        // Binden der Schriftgröße an die Breite des Fensters
        fontSize.bind(stage.widthProperty().divide(20));

        // Anwenden der Schriftgröße auf vbox
        vbox.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.intValue(), "px;"));

        Scene scene = new Scene(vbox, 800, 600);
        stage.setTitle("Server GUI");
        stage.setScene(scene);
        stage.show();

        scrollPane.prefWidthProperty().bind(scene.widthProperty());
        scrollPane.prefHeightProperty().bind(scene.heightProperty().subtract(menuBar.getHeight() + cmdField.getHeight() + 20));


    }

    private void changeFontSize(int size) {
        this.sizeValue = sizeValue + size;

        logArea.setStyle("-fx-font-size: " + sizeValue);
        cmdField.setStyle("-fx-font-size: " + sizeValue);
        menuBar.setStyle("-fx-font-size: " + sizeValue);
    }


    private void standardFontSizeMethod() {
        this.sizeValue = 13;

        logArea.setStyle("-fx-font-size: " + 13);
        cmdField.setStyle("-fx-font-size: " + 13);
        menuBar.setStyle("-fx-font-size: " + 13);
    }

    @Override
    public void handleFatalError(Exception e) {
        Platform.runLater(() -> {
            String log = "Ⓧ [Fatal Error]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #fd0000;-fx-font-weight:bold;");
            Text t2 = new Text(e.getMessage() + "\n");
            t2.setStyle("-fx-font-weight:normal;");
            logArea.getChildren().addAll(t1, t2);
            standardFontSizeMethod();
        });
    }

    @Override
    public void handleError(Exception e) {
        Platform.runLater(() -> {
            String log = "Ⓧ [Error]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #fd6e00;-fx-font-weight:bold;");
            Text t2 = new Text(e.getMessage() + "\n");
            t2.setStyle("-fx-font-weight:normal;");
            logArea.getChildren().addAll(t1, t2);
            standardFontSizeMethod();
        });
    }

    @Override
    public void handleWarning(String warning) {
        Platform.runLater(() -> {
            String log = "⚠ [Warning]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #d2c800;-fx-font-weight:bold;");
            Text t2 = new Text(warning + "\n");
            t2.setStyle("-fx-font-weight:normal;");
            logArea.getChildren().addAll(t1, t2);
            standardFontSizeMethod();
        });
    }

    @Override
    public void handleInfo(String info) {
        Platform.runLater(() -> {
            String log = "ⓘ [Info]: ";
            Text t1 = new Text(log);
            t1.setStyle("-fx-fill: #00bafd;-fx-font-weight:bold;");
            Text t2 = new Text(info + "\n");
            t2.setStyle("-fx-font-weight:normal;");
            logArea.getChildren().addAll(t1, t2);
            standardFontSizeMethod();
        });
    }

}
