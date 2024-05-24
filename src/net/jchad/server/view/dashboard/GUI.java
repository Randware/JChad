package net.jchad.server.view.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.nio.file.Path;

public class GUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);

        Scene scene = new Scene(root, 700,450);


        stage.setTitle("JChad Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
