module net.jchad {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;

    opens net.jchad.client to javafx.fxml;
    exports net.jchad.installer.gui;
    exports net.jchad.installer.serializable to com.google.gson;
}