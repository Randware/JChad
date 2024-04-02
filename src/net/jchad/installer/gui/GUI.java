package net.jchad.installer.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import net.jchad.installer.config.ConfigManager;
import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.BarDisplay;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUI extends Application implements BarDisplay {

    private ProgressBar progressBar;
    private ConfigManager configManager = new ConfigManager();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws InstantiationException {
        primaryStage.setTitle("JChad Setup");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to the JChad setup!");
        vbox.getChildren().add(welcomeLabel);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        Button chooseDirectoryButton = new Button("Choose Installation Directory");
        chooseDirectoryButton.setOnAction(e -> {
            Path directory = directoryChooser.showDialog(primaryStage).toPath();
            configManager.setDownloadPath(directory.toString());
            chooseDirectoryButton.setText(directory.toString());
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

        progressBar = new ProgressBar(); // Initialisierung der progressBar-Variable
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(26);
        vbox.getChildren().add(progressBar);

        Button installButton = new Button("Install");
        installButton.setOnAction(e -> {
            Task<Void> installTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Perform your installation process here
                    configManager.process();
                    return null;
                }
            };

            installTask.setOnRunning(event -> {
                // Update UI to show progress
                progressBar.setProgress(-1); // Indeterminate progress
                installButton.setDisable(true); // Disable the button during installation
            });

            installTask.setOnSucceeded(event -> {
                // Update UI after installation is complete
                progressBar.setProgress(1); // Set progress to complete
                progressBar.setStyle("-fx-accent: green;");
                installButton.setDisable(false); // Enable the button
            });

            installTask.setOnFailed(event -> {
                // Handle failure

                progressBar.setProgress(0); // Set progress to zero
                installButton.setDisable(false); // Enable the button
            });

            new Thread(installTask).start(); // Start the installation task in a new thread
        });

        vbox.getChildren().add(installButton);

        Scene scene = new Scene(vbox, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
        hook("RepoDownloader");
    }

    @Override
    public void update(Bar bar) {

    }

    @Override
    public void updateOnFailed(Bar bar) {

        if (bar.getBarStatus().getException().isPresent()) {
            System.out.println(bar.getBarStatus().getException().get().getMessage());
            Pattern getErrorCode = Pattern.compile(".*([\\d]{3}).*");
            Matcher errorCode
            if ()
        }
    }

    @Override
    public void updateOnStart(Bar bar) {

    }

    @Override
    public void updateOnEnd(Bar bar) {

    }

    @Override
    public void updateOnUpdate(Bar bar) {

        Platform.runLater(() -> progressBar.setProgress((double) bar.getCurrentProgress() / bar.getMaxProgress()));
    }
}