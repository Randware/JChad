package net.jchad.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.Socket;

public class ClientTestEventHandler implements EventHandler<ActionEvent> {



    @Override
    public void handle(ActionEvent actionEvent) {
        System.out.println("Test");
    }
}
