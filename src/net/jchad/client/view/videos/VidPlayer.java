package net.jchad.client.view.videos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class VidPlayer extends Application {
    private MediaPlayer mediaPlayer;
    private Media media;
    private MediaView mediaView;

    private File videoFile = new File("src/net/jchad/client/view/videos/monkerizz2.mp4");
    private String mediaUrl = videoFile.toURI().toString();

    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        media = new Media(mediaUrl);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setOnReady(primaryStage::sizeToScene);
        mediaView = new MediaView(mediaPlayer);
        mediaView.preserveRatioProperty().set(true);

        borderPane.setCenter(mediaView);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("monke Rizz");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}