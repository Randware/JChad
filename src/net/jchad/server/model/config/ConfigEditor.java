package net.jchad.server.model.config;

import net.jchad.server.model.config.store.internalSettings.InternalSettings;
import net.jchad.server.model.config.store.serverSettings.ServerSettings;

// TODO: implement functionality for writing changed values to config files
public class ConfigEditor {
    private ConfigManager configManager;
    private ServerSettings serverSettings;
    private InternalSettings internalSettings;

    public ConfigEditor(ConfigManager configManager) {
        this.configManager = configManager;
        this.serverSettings = configManager.getConfig().getServerSettings();
        this.internalSettings = configManager.getConfig().getInternalSettings();
    }

    public void setRequiresPassword(boolean requiresPassword) {
        serverSettings.setRequiresPassword(requiresPassword);
    }

    public void setPassword(String password) {
        serverSettings.setPassword(password);
    }

    public void setWhitelist(boolean whitelist) {
        serverSettings.setWhitelist(whitelist);
    }

    public void setBlacklist(boolean blacklist) {
        serverSettings.setBlacklist(blacklist);
    }

    public void setStrictlyAnonymous(boolean strictlyAnonymous) {
        serverSettings.setStrictlyAnonymous(strictlyAnonymous);
    }

    public void setEncrypted(boolean encrypted) {
        serverSettings.setEncrypted(encrypted);
    }

    public void setPort(int port) {
        serverSettings.setPort(port);
    }

    public void setMaxConfigWatcherRestarts(int maxConfigWatcherRestarts) {
        internalSettings.setMaxConfigWatcherRestarts(maxConfigWatcherRestarts);
    }

    public void setConfigWatcherRestartCountResetMilliseconds(int configWatcherRestartCountResetMilliseconds) {
        internalSettings.setConfigWatcherRestartCountResetMilliseconds(configWatcherRestartCountResetMilliseconds);
    }
}
