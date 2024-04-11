module net.jchad {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens net.jchad.client to javafx.fxml;
    opens net.jchad.server.model.config to com.fasterxml.jackson.databind;
    exports net.jchad.installer.gui;
    exports net.jchad.installer.serializable to com.google.gson;
    opens net.jchad.client.model.client to javafx.fxml;
}