package net.jchad.server.model.config.store.internalSettings;

import net.jchad.server.model.config.ConfigWatcher;

/**
 * Stores internal settings the server uses to function.
 * These values have sensible defaults, but if the user wants to modify them, they can.
 */
public class InternalSettings {
    /**
     * The maximum restart attempts for the {@link ConfigWatcher}, if it crashes.
     */
    private int maxConfigWatcherRestarts;

    /**
     * The amount of milliseconds after which the restart counter for the {@link ConfigWatcher} gets reset.
     * Set to negative number to never reset the restart counter.
     */
    private int configWatcherRestartCountResetMilliseconds;

    /**
     * Default {@link InternalSettings} constructor
     */
    public InternalSettings() {
        InternalSettings defaultInternalSettings = DefaultInternalSettings.get();

        this.maxConfigWatcherRestarts = defaultInternalSettings.getMaxConfigWatcherRestarts();
        this.configWatcherRestartCountResetMilliseconds = defaultInternalSettings.getConfigWatcherRestartCountResetMilliseconds();
    }

    /**
     * Parameterized {@link InternalSettings} constructor
     *
     * @param maxConfigWatcherRestarts The maximum restart attempts for the {@link ConfigWatcher}, if it crashes.
     * @param configWatcherRestartCountResetMilliseconds The amount of milliseconds after which the restart counter for the {@link ConfigWatcher} gets reset.
     *                                                   Set to negative number to never reset the restart counter.
     */
    public InternalSettings(int maxConfigWatcherRestarts, int configWatcherRestartCountResetMilliseconds) {
        this.maxConfigWatcherRestarts = maxConfigWatcherRestarts;
        this.configWatcherRestartCountResetMilliseconds = configWatcherRestartCountResetMilliseconds;
    }

    /**
     * @return The maximum restart attempts for the {@link ConfigWatcher}, if it crashes.
     */
    public int getMaxConfigWatcherRestarts() {
        return maxConfigWatcherRestarts;
    }

    /**
     * @param maxConfigWatcherRestarts The maximum restart attempts for the {@link ConfigWatcher}, if it crashes.
     */
    public void setMaxConfigWatcherRestarts(int maxConfigWatcherRestarts) {
        this.maxConfigWatcherRestarts = maxConfigWatcherRestarts;
    }

    /**
     * @return The amount of milliseconds after which the restart counter for the {@link ConfigWatcher} gets reset.
     */
    public int getConfigWatcherRestartCountResetMilliseconds() {
        return configWatcherRestartCountResetMilliseconds;
    }

    /**
     * @param configWatcherRestartCountResetMilliseconds The amount of milliseconds after which the restart counter for the {@link ConfigWatcher} gets reset.
     *                                                   Set to negative number to never reset the restart counter.
     */
    public void setConfigWatcherRestartCountResetMilliseconds(int configWatcherRestartCountResetMilliseconds) {
        this.configWatcherRestartCountResetMilliseconds = configWatcherRestartCountResetMilliseconds;
    }
}
