package net.jchad.client.view.gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;

public class InputPrompt extends Alert {
    private String title;
    private String message;
    private String input = null;
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

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Submit");
        okButton.setDisable(true);

        okButton.setOnAction((actionEvent -> {
            input = inputField.getText();
        }));

        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Adjust scaling based on screen resolution
        applyScaling();
    }

    private void applyScaling() {
        double dpi = Screen.getPrimary().getDpi();
        double scaleFactor = dpi / 96.0; // 96 DPI is considered the standard DPI

        // Adjust font sizes
        double baseFontSize = 12; // base font size in points
        double scaledFontSize = baseFontSize * scaleFactor;

        // Apply scaled font size to the elements
        GridPane grid = (GridPane) getDialogPane().getContent();
        Label messageLabel = (Label) grid.getChildren().get(0);
        messageLabel.setStyle("-fx-font-size: " + scaledFontSize + "px");

        inputField.setStyle("-fx-font-size: " + scaledFontSize + "px");
    }

    /**
     * Returns the user input string, null if the user pressed the cancel button.
     *
     * @return the user input, null if the user pressed the cancel button.
     */
    public String getInput() {
        return input;
    }
}
