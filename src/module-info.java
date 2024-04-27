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


    opens net.jchad.client to javafx.fxml;
    opens net.jchad.server.model.config to com.fasterxml.jackson.databind;
    opens net.jchad.server.model.networking.versioning to com.google.gson;
    exports net.jchad.installer.gui;
    exports net.jchad.installer.serializable to com.google.gson;
    opens net.jchad.client.model.client to javafx.fxml;
    opens net.jchad.server.model.common to com.fasterxml.jackson.databind;
    exports net.jchad.server.view to javafx.graphics;
    exports net.jchad.tests;
    opens net.jchad.server.model.config.store to com.fasterxml.jackson.databind;
    opens net.jchad.server.model.config.store.internalSettings to com.fasterxml.jackson.databind;
    opens net.jchad.server.model.config.store.serverSettings to com.fasterxml.jackson.databind;
}