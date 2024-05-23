package net.jchad.client.view.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class InputPrompt extends Alert {
    private String title;
    private String message;
    private TextField inputField;

    public InputPrompt(AlertType alertType, String title, String message) {
        super(alertType);
        this.title = title;
        this.message = message;
        this.inputField = new TextField();
        setInfo();
    }

    private void setInfo() {
        setTitle(title);
        setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setHgap(10);
        grid.setVgap(10);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 1em");
        grid.add(messageLabel, 0, 0);

        inputField.setStyle("-fx-font-size: 1em");
        grid.add(inputField, 0, 1);
        GridPane.setHgrow(inputField, Priority.ALWAYS);

        getDialogPane().setContent(grid);
    }

    public String getInput() {
        return inputField.getText();
    }
}
