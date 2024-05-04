module net.jchad {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.params;
    requires org.jline;
    requires org.fxmisc.richtext;
    requires jdk.unsupported;


    opens net.jchad.client to javafx.fxml;
    opens net.jchad.shared.networking.packets to com.google.gson;
    opens net.jchad.shared.networking.ip to com.google.gson;
    opens net.jchad.server.model.config to com.fasterxml.jackson.databind;
    exports net.jchad.installer.gui;
    exports net.jchad.installer.serializable to com.google.gson;
    opens net.jchad.client.model.client to javafx.fxml;
    opens net.jchad.shared.common to com.fasterxml.jackson.databind;
    exports net.jchad.server.view to javafx.graphics;
    opens net.jchad.server.model.config.store to com.fasterxml.jackson.databind;
    opens net.jchad.server.model.chats to com.google.gson;
}