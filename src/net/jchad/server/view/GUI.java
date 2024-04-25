package net.jchad.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    /*private Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-logo-information-library-business-information-5abe49fe3f2365.1117500215224202222586.png").toExternalForm());
    private ImageView imageView = new ImageView(image);*/

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

        test();
    }

    public void test(){
        String warning = "WARNING ______ test _ test _ test _ test_ test_ test_ test test test______________________________________________________________________________________________________________  test";
        handleFatalError(new Exception("Fatal error"));
        handleError(new Exception("error"));
        for (int i = 0; i < 1000; i++) {
            handleWarning(warning);
        }
    }

    private void changeFontSize(int size) {
        this.sizeValue = sizeValue + size;
        //double aspectRatio = imageView.getImage().getWidth() / imageView.getImage().getHeight();
        //double newWidth = sizeValue * 10 * aspectRatio;

        logArea.setStyle("-fx-font-size: " +sizeValue);
        cmdField.setStyle("-fx-font-size: " +sizeValue);

        /*imageView.setFitHeight(sizeValue * 10);
        imageView.setFitWidth(newWidth);
        imageView.setPreserveRatio(true);*/
    }


    private void standardFontSizeMethod(){
        this.sizeValue = 13;
        /*double aspectRatio = imageView.getImage().getWidth() / imageView.getImage().getHeight();
        double newWidth = sizeValue * 10 * aspectRatio;*/

        logArea.setStyle("-fx-font-size: " + 13);
        cmdField.setStyle("-fx-font-size: " + 13);

        /*imageView.setFitHeight(sizeValue * 10);
        imageView.setFitWidth(newWidth);
        imageView.setPreserveRatio(true);*/
    }

    @Override
    public void handleFatalError(Exception e) {
        //Platform.runLater(() -> logArea.appendText("[Fatal Error]: " + e.getMessage() + "\n"));
        /*Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-computer-icons-warning-sign-symbol.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);*/
        String log = "Ⓧ [Fatal Error]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #fd0000;-fx-font-weight:bold;");
        Text t2 = new Text(e.getMessage() + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        //logArea.getChildren().remove(imageView);
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public void handleError(Exception e) {
        //Platform.runLater(() -> logArea.appendText("[Error]: " + e.getMessage() + "\n"));
        /*Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-computer-icons-warning-sign-sin.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);*/
        String log = "Ⓧ [Error]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #fd6e00;-fx-font-weight:bold;");
        Text t2 = new Text(e.getMessage() + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        //logArea.getChildren().remove(imageView);
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public void handleWarning(String warning) {
        /*Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-warning-sign-exclamation-mark-warning-sign.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);*/
        String log = "⚠ [Warning]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #d2c800;-fx-font-weight:bold;");
        Text t2 = new Text(warning + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        //logArea.getChildren().remove(imageView);
        logArea.getChildren().addAll(t1, t2);
        standardFontSizeMethod();
    }

    @Override
    public void handleInfo(String info) {
        /*Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-logo-information-library-business-information-5abe49fe3f2365.1117500215224202222586.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.autosize();
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);*/
        String log = "ⓘ [Info]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #00bafd;-fx-font-weight:bold;");
        Text t2 = new Text(info + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        //logArea.getChildren().remove(imageView);
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
