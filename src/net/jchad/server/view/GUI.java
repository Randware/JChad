package net.jchad.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;


// Responsible for displaying server output in GUI mode
public class GUI extends Application implements MessageHandler {
    private ServerController server;
    private TextFlow logArea = new TextFlow();
    private TextField cmdField = new TextField();
    private double sizeValue= 13;
    private ScrollPane scrollPane = new ScrollPane();

    //launch method
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        server = new ServerController(this);
        server.startServer();

        cmdField.setPromptText("Enter command here...");

        MenuBar menuBar = new MenuBar();
        menuBar.setPadding(Insets.EMPTY);

        Menu settingsMenu = new Menu("Settings");

        Menu fontsSubMenu = new Menu("Fonts");

        MenuItem increaseFontSize = new MenuItem("increase Font size");
        MenuItem standardFontSize = new MenuItem("standard Font size");
        MenuItem decreaseFontSize = new MenuItem("decrease Font size");

        fontsSubMenu.getItems().addAll(increaseFontSize, standardFontSize, decreaseFontSize);

        settingsMenu.getItems().add(fontsSubMenu);
        
        menuBar.getMenus().add(settingsMenu);

        scrollPane.setContent(logArea);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox vbox = new VBox(menuBar, scrollPane, cmdField);

        increaseFontSize.setOnAction(e -> changeFontSize(2));
        standardFontSize.setOnAction(e -> standardFontSizeMethod());
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
        this.sizeValue = sizeValue + size;

        logArea.setStyle("-fx-font-size: " +sizeValue);
        cmdField.setStyle("-fx-font-size: " +sizeValue);

    }


    private void standardFontSizeMethod(){
        this.sizeValue = 13;

        logArea.setStyle("-fx-font-size: " + 13);
        cmdField.setStyle("-fx-font-size: " + 13);

    }

    @Override
    public void handleFatalError(Exception e) {
        String log = "Ⓧ [Fatal Error]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #fd0000;-fx-font-weight:bold;");
        Text t2 = new Text(e.getMessage() + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public void handleError(Exception e) {
        String log = "Ⓧ [Error]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #fd6e00;-fx-font-weight:bold;");
        Text t2 = new Text(e.getMessage() + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public void handleWarning(String warning) {
        String log = "⚠ [Warning]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #d2c800;-fx-font-weight:bold;");
        Text t2 = new Text(warning + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public void handleInfo(String info) {
        String log = "ⓘ [Info]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #00bafd;-fx-font-weight:bold;");
        Text t2 = new Text(info + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return 0;
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return 0;
    }

    @Override
    public ImageProducer getSource() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

}
