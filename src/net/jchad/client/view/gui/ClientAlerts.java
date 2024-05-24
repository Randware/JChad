package net.jchad.client.view.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

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
        Alert alert = new Alert(getAlertType());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getButtonTypes().clear();
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getDialogPane().getButtonTypes().add(okButton);

        alert.showAndWait();
    }
}