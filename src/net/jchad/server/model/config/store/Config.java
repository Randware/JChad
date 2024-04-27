package net.jchad.server.model.config.store;

import net.jchad.server.model.config.store.internalSettings.InternalSettings;
import net.jchad.server.model.config.store.serverSettings.ServerSettings;
import net.jchad.server.model.networking.ip.IPAddress;

import java.util.ArrayList;

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

    private ArrayList<IPAddress> whitelist;

    private ArrayList<IPAddress> blacklist;

    /**
     * Create a new default Config
     */
    public Config() {
        this.serverSettings = new ServerSettings();
        this.internalSettings = new InternalSettings();
        this.whitelist = new ArrayList<>();
        this.blacklist = new ArrayList<>();
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

    public ArrayList<IPAddress> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(ArrayList<IPAddress> whitelist) {
        this.whitelist = whitelist;
    }

    public ArrayList<IPAddress> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(ArrayList<IPAddress> blacklist) {
        this.blacklist = blacklist;
    }
}
