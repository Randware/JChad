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

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;


// Responsible for displaying server output in GUI mode
public class GUI extends Application implements MessageHandler {
    private ServerController server;
    private TextFlow logArea;
    private TextField cmdField;
    private double sizeValue= 13;
    private Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-logo-information-library-business-information-5abe49fe3f2365.1117500215224202222586.png").toExternalForm());
    private ImageView imageView = new ImageView(image);

    //launch method
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        server = new ServerController(this);
        server.startServer();

        logArea = new TextFlow();

        cmdField = new TextField();
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

        VBox vbox = new VBox(menuBar, logArea, cmdField);

        increaseFontSize.setOnAction(e -> changeFontSize(2));
        standardFontSize.setOnAction(e -> standardFontSizeMethod());
        decreaseFontSize.setOnAction(e -> changeFontSize(-2));

        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 10, 10));
        VBox.setVgrow(logArea, Priority.ALWAYS);
        vbox.setMaxHeight(Double.MAX_VALUE);
        vbox.setStyle("-fx-font-size: 13px;");

        String warning = "Darius du fickkopf";
        handleFatalError(new Exception("Fatal error"));
        handleError(new Exception("error"));
        handleWarning(warning);

        Scene scene = new Scene(vbox, 800, 600);
        stage.setTitle("Server GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void changeFontSize(int size) {
        this.sizeValue = sizeValue + size;
        double aspectRatio = imageView.getImage().getWidth() / imageView.getImage().getHeight();
        double newWidth = sizeValue * 10 * aspectRatio;

        Platform.runLater(() -> logArea.setStyle("-fx-font-size: " +sizeValue));
        Platform.runLater(() -> cmdField.setStyle("-fx-font-size: " +sizeValue));

        Platform.runLater(() -> imageView.setFitHeight(sizeValue * 10));
        Platform.runLater(() -> imageView.setFitWidth(newWidth));
        Platform.runLater(() -> imageView.setPreserveRatio(true));
    }


    private void standardFontSizeMethod(){
        this.sizeValue = 13;
        double aspectRatio = imageView.getImage().getWidth() / imageView.getImage().getHeight();
        double newWidth = sizeValue * 10 * aspectRatio;

        Platform.runLater(() -> logArea.setStyle("-fx-font-size: " + 13));
        Platform.runLater(() -> cmdField.setStyle("-fx-font-size: " + 13));

        Platform.runLater(() -> imageView.setFitHeight(sizeValue * 10));
        Platform.runLater(() -> imageView.setFitWidth(newWidth));
        Platform.runLater(() -> imageView.setPreserveRatio(true));
    }

    @Override
    public void handleFatalError(Exception e) {
        //Platform.runLater(() -> logArea.appendText("[Fatal Error]: " + e.getMessage() + "\n"));
        Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-computer-icons-warning-sign-symbol.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);
        String log = "[Fatal Error]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #fd0000;-fx-font-weight:bold;");
        Text t2 = new Text(e.getMessage() + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        Platform.runLater(() -> logArea.getChildren().remove(imageView));
        Platform.runLater(() -> logArea.getChildren().addAll(imageView, t1, t2));
        standardFontSizeMethod();
    }

    @Override
    public void handleError(Exception e) {
        //Platform.runLater(() -> logArea.appendText("[Error]: " + e.getMessage() + "\n"));
        Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-computer-icons-warning-sign-sin.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);
        String log = "[Error]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #fd6e00;-fx-font-weight:bold;");
        Text t2 = new Text(e.getMessage() + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        Platform.runLater(() -> logArea.getChildren().remove(imageView));
        Platform.runLater(() -> logArea.getChildren().addAll(imageView, t1, t2));
        standardFontSizeMethod();
    }

    @Override
    public void handleWarning(String warning) {
        Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-warning-sign-exclamation-mark-warning-sign.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);
        String log = "[Warning]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #d2c800;-fx-font-weight:bold;");
        Text t2 = new Text(warning + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        Platform.runLater(() -> logArea.getChildren().remove(imageView));
        Platform.runLater(() -> logArea.getChildren().addAll(imageView, t1, t2));
        standardFontSizeMethod();
    }

    @Override
    public void handleInfo(String info) {
        Image image = new Image(getClass().getResource("/net/jchad/server/view/pictures/kisspng-logo-information-library-business-information-5abe49fe3f2365.1117500215224202222586.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        //imageView.autosize();
        imageView.setFitHeight(sizeValue);
        imageView.setFitWidth(sizeValue);
        String log = "[Info]: ";
        Text t1 = new Text(log);
        t1.setStyle("-fx-fill: #00bafd;-fx-font-weight:bold;");
        Text t2 = new Text(info + "\n");
        t2.setStyle("-fx-font-weight:normal;");
        Platform.runLater(() -> logArea.getChildren().remove(imageView));
        Platform.runLater(() -> logArea.getChildren().addAll(imageView, t1, t2));
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
