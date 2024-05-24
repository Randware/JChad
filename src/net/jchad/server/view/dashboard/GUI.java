package net.jchad.server.view.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.nio.file.Path;

public class GUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/server/Dashboard.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);

        Scene scene = new Scene(root, 600,300);

        stage.setTitle("JChad Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
