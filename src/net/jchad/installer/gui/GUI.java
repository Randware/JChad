package net.jchad.installer.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.jchad.installer.config.ConfigManager;

import java.nio.file.Path;

public class GUI extends Application {

    private ConfigManager configManager = new ConfigManager();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JChad Setup");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Welcome to the JChad setup!");
        vbox.getChildren().add(welcomeLabel);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        Button chooseDirectoryButton = new Button("Choose Installation Directory");
        chooseDirectoryButton.setOnAction(e -> {
            Path directory = directoryChooser.showDialog(primaryStage).toPath();
            configManager.setDownloadPath(directory.toString());
        });
        vbox.getChildren().add(chooseDirectoryButton);

        ComboBox<String> softwareComboBox = new ComboBox<>();
        softwareComboBox.getItems().addAll("Client and Server", "Server", "Client");
        softwareComboBox.setPromptText("Select Software to Install");
        softwareComboBox.setOnAction(e -> {
            int selectedIndex = softwareComboBox.getSelectionModel().getSelectedIndex();
            configManager.setSoftwareToInstall(selectedIndex);
        });
        vbox.getChildren().add(softwareComboBox);

        Button installButton = new Button("Install");
        installButton.setOnAction(e -> configManager.process());
        vbox.getChildren().add(installButton);

        Scene scene = new Scene(vbox, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}