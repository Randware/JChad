package net.jchad.client.model.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.connection.ConnectionDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This class manages the {@link ClientConfig}.
 */
public class ClientConfigManager {
    private final Gson gson;

    /**
     * This is where the config will be stored.
     */
    private static final Path configSavePath = Path.of("./config.json");
    private ViewCallback viewCallback;

    private ClientConfig config = new ClientConfig();

    /**
     * This specifies if the config should be saved if changes are made. Changes
     * will not be saved if the program is using a default config, so it does not
     * overwrite the existing config.
     */
    private boolean saveConfig;

    public ClientConfigManager(ViewCallback viewCallback) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.viewCallback = viewCallback;
        this.saveConfig = true;

        try {
            loadConfig();
        } catch (JsonParseException | IOException e) {
            viewCallback.handleError(new IOException("Failed loading client configuration, using default configuration." +
                    "Configuration changes made, will not be saved.", e));

            this.config = new ClientConfig();

            saveConfig = false;
        }

    }

    /**
     * Tries to write the current {@link ClientConfig} to file.
     *
     * @throws IOException if writing to the file was not successful.
     */
    private void saveConfig() throws IOException {
        if(!saveConfig) {
            return;
        }

        if(!Files.exists(configSavePath)) {
            Files.createFile(configSavePath);
        }

        Files.writeString(configSavePath, gson.toJson(config));
    }

    /**
     * Tries to load a saved config file into the current {@link ClientConfig}.
     *
     * @throws IOException if an IOException occurred when writing or saving the file.
     * @throws JsonParseException if the file couldn't be deserialized from the file.
     */
    private void loadConfig() throws IOException, JsonParseException {
        if(!Files.exists(configSavePath)) {
            saveConfig();
        }

        this.config = gson.fromJson(Files.readString(configSavePath), ClientConfig.class);
    }

    /**
     * Adds a connection to the current {@link ClientConfig} and tries to save
     * the newly modified {@link ClientConfig} to file.
     *
     * @param connection the {@link ConnectionDetails} instance that should be added.
     */
    public void addConnection(ConnectionDetails connection) {
        config.getStoredConnections().add(connection);

        try {
            saveConfig();
        } catch (IOException e) {
            viewCallback.handleError(new IOException("The connection could not be saved in the config file.", e));
        }
    }

    /**
     * Removes a connection from the current {@link ClientConfig} and tries to save
     * the newly modified {@link ClientConfig} to file.
     *
     * @param connection the {@link ConnectionDetails} instance that should be removed.
     */
    public void removeConnection(ConnectionDetails connection) {
        config.getStoredConnections().remove(connection);

        try {
            saveConfig();
        } catch (IOException e) {
            viewCallback.handleError(new IOException("The connection could not be removed in the config file.", e));
        }
    }

    /**
     * Returns all currently saved {@link ClientConfig} instances.
     *
     * @return an {@link ArrayList} containing all currently saved
     * {@link ClientConfig} instances.
     */
    public ArrayList<ConnectionDetails> getConnections() {
        return new ArrayList<>(config.getStoredConnections());
    }
}
