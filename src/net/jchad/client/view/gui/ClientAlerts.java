package net.jchad.client.view.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

import java.util.Arrays;

public class ClientAlerts extends Alert {
    private String title;
    private String message;

    public ClientAlerts(AlertType alertType, String title, String message) {
        super(alertType);
        this.title = title;
        this.message = message;
        showAlert();
    }

    private void showAlert() {
        try {
            Alert alert = new Alert(getAlertType());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.getDialogPane().getButtonTypes().clear();
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getDialogPane().getButtonTypes().add(okButton);

            alert.showAndWait();
        } catch (Exception e) {
            // Create a new alert to show the exception details
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.initModality(Modality.APPLICATION_MODAL);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An unexpected error occurred.");
            errorAlert.setContentText(e.getMessage());

            // Append the stack trace to the content text
            StringBuilder sb = new StringBuilder(errorAlert.getContentText());
            sb.append("\n\nStack Trace:\n").append(Arrays.toString(e.getStackTrace()));
            errorAlert.setContentText(sb.toString());

            errorAlert.getDialogPane().getButtonTypes().clear();
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            errorAlert.getDialogPane().getButtonTypes().add(okButton);

            errorAlert.showAndWait();
        }
    }
}
