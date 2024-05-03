package net.jchad.server.model.config;

import net.jchad.server.model.config.store.InternalSettings;
import net.jchad.server.model.config.store.ServerSettings;

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
        saveConfig();
    }

    public void setPassword(String password) {
        serverSettings.setPassword(password);
        saveConfig();
    }

    public void setWhitelist(boolean whitelist) {
        serverSettings.setWhitelist(whitelist);
        saveConfig();
    }

    public void setBlacklist(boolean blacklist) {
        serverSettings.setBlacklist(blacklist);
        saveConfig();
    }

    public void setStrictlyAnonymous(boolean strictlyAnonymous) {
        serverSettings.setStrictlyAnonymous(strictlyAnonymous);
        saveConfig();
    }

    public void setEncrypted(boolean encrypted) {
        serverSettings.setEncrypted(encrypted);
        saveConfig();
    }

    public void setPort(int port) {
        serverSettings.setPort(port);
        saveConfig();
    }

    public void setMaxPathWatcherRestarts(int maxPathWatcherRestarts) {
        internalSettings.setMaxPathWatcherRestarts(maxPathWatcherRestarts);
        saveConfig();
    }

    public void setPathWatcherRestartCountResetMilliseconds(int PathWatcherRestartCountResetMilliseconds) {
        internalSettings.setPathWatcherRestartCountResetMilliseconds(PathWatcherRestartCountResetMilliseconds);
        saveConfig();
    }

    private void saveConfig() {
        configManager.saveServerConfig();
    }
}
