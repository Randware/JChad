package net.jchad.client.view.videos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class VidPlayer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://www.youtube.com/embed/8Qc29mubbzk?autoplay=1");

        Scene scene = new Scene(webView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("YouTube Video Player");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}