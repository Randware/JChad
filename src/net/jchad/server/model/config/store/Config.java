package net.jchad.server.model.config.store;

import net.jchad.server.model.config.store.internalSettings.InternalSettings;
import net.jchad.server.model.config.store.serverSettings.ServerSettings;

/**
 * This class represents the server configuration. It stores other sub-configurations.
 */
public class Config {
    /**
     * Stores the current {@link ServerSettings}
     */
    private ServerSettings serverSettings;

    /**
     * Stores the current {@link InternalSettings}
     */
    private InternalSettings internalSettings;

    /**
     * Create a new default Config
     */
    public Config() {
        this.serverSettings = new ServerSettings();
        this.internalSettings = new InternalSettings();
    }

    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    public void setServerSettings(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    public InternalSettings getInternalSettings() {
        return internalSettings;
    }

    public void setInternalSettings(InternalSettings internalSettings) {
        this.internalSettings = internalSettings;
    }
}
