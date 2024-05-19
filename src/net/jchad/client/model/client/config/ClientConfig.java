package net.jchad.client.model.client.config;

import net.jchad.client.model.store.connection.ConnectionDetails;

import java.util.ArrayList;

/**
 * This is the configuration for the client. Default values will also be stored here.
 */
public class ClientConfig {
    private ArrayList<ConnectionDetails> storedConnections = new ArrayList<>();

    public ClientConfig() {
    }

    public ArrayList<ConnectionDetails> getStoredConnections() {
        return storedConnections;
    }

    public void setStoredConnections(ArrayList<ConnectionDetails> storedConnections) {
        this.storedConnections = storedConnections;
    }
}
