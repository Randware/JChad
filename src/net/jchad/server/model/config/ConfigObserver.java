package net.jchad.server.model.config;

/**
 * Implementing class instances can be passed to the {@link ConfigManager}
 * to be notified of config file changes.
 */
public interface ConfigObserver {
    /**
     * Called when a config change is registered.
     */
    void configUpdated();
}
