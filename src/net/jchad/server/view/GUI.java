package net.jchad.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jchad.server.controller.ServerController;
import net.jchad.server.model.error.MessageHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

// Responsible for displaying server output in GUI mode
public class GUI extends Application implements MessageHandler {
    private ServerController server;
    private TextArea logArea;
    private TextField cmdField;
    private double sizeValue= 13;
    //launch method
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        server = new ServerController(this);

        logArea = new TextArea();
        logArea.setEditable(false);

        cmdField = new TextField();
        cmdField.setPromptText("Enter command here...");


        MenuBar menuBar = new MenuBar();

        Menu settingsMenu = new Menu("Settings");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size");
        MenuItem standardFontSize = new MenuItem("standard Font size");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);

        menuBar.getMenus().add(settingsMenu);

        VBox vbox = new VBox(menuBar, logArea, cmdField);

        increaseFontSize.setOnAction(e -> changeFontSize(2));
        standardFontSize.setOnAction(e -> standardFontSizeMethod(vbox));
        decreaseFontSize.setOnAction(e -> changeFontSize(-2));

        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 10, 10));
        VBox.setVgrow(logArea, Priority.ALWAYS);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setStyle("-fx-font-size: 13px;");

        Scene scene = new Scene(vbox, 800, 600);
        stage.setTitle("Server GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void changeFontSize(int size) {
        Platform.runLater(() -> logArea.setStyle("-fx-font-size: " +(sizeValue + size)));
        Platform.runLater(() -> cmdField.setStyle("-fx-font-size: " +(sizeValue + size)));
        this.sizeValue = sizeValue + size;
    }

    private void standardFontSizeMethod(VBox vBox){
        Platform.runLater(() -> logArea.setStyle("-fx-font-size: " + 13));
        Platform.runLater(() -> cmdField.setStyle("-fx-font-size: " + 13));
        this.sizeValue = 13;
    }

    @Override
    public void handleFatalError(Exception e) {

    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void handleWarning(String warning) {

    }

    @Override
    public void handleInfo(String info) {

    }


}
