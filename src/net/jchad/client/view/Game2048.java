package net.jchad.client.view;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;



public class Game2048 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("2048 by JChad");
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://www.mathsisfun.com/games/a/2048/index.html");
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                webEngine.executeScript("""
            var elements = document.getElementsByClassName('game-explanation');
            while(elements.length > 0) {
                elements[0].parentNode.removeChild(elements[0]);
            }
            var elements1 = document.getElementsByClassName('game-intro');
                                        for (var i = 0; i < elements1.length; i++) {
                                            elements1[i].textContent = 'Welcome to 2048 made by the JChad Devs';
                                        }
        """);
            }
        });



        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 900, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
