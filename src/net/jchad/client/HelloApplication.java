package net.jchad.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("JChad-client");
        StackPane stackPane = new StackPane();
        stage.setScene(new Scene(stackPane));

        Text text = new Text("Welcome to the client of JChad");
        text.setFont(Font.font("Arial",50));

        StackPane.setAlignment(text, Pos.CENTER);
        stackPane.getChildren().add(text);
        stage.setMinWidth(text.getWrappingWidth());
        Button btn = new Button();
        btn.setText("Click me!");
        btn.setOnAction(new ClientTestEventHandler());
        StackPane.setAlignment(btn, Pos.BOTTOM_CENTER);
        stackPane.getChildren().add(btn);
        stage.setMinHeight(500);
        stage.show();
    }


}