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
     * This variable controls the {@code Thread.sleep()} duration in every iteration of a loop on each server-client connection:
     * <ul>
     * <li>The variable specifies the delay (in milliseconds) between each iteration of the loop.</li>
     * <li>For each connection, the server pauses for this duration before attempting to read/write data from the socket streams again.</li>
     * <li>Increasing this value will reduce the performance impact of a single connection.</li>
     * <li>However, be aware that higher values can lead to increased latency between the server and the client.</li>
     * <li>If this value is set to less than 0, a default value of 100 milliseconds will be used.</li>
     * </ul>
     */
    private long connectionRefreshIntervalMillis;

    /**
     * This determines how many times the client is allowed to send falsy data to the server during the {@link net.jchad.server.model.networking.versioning versioning} process.
     * If this limit gets exceeded the connection will close.
     * If this gets set to a negative number the default value (3) get used instead.
     */
    private int retriesOnMalformedJSONduringVersioning;

    /**
     * Default {@link InternalSettings} constructor
     */
    public InternalSettings() {
        InternalSettings defaultInternalSettings = DefaultInternalSettings.get();

        this.maxConfigWatcherRestarts = defaultInternalSettings.getMaxConfigWatcherRestarts();
        this.configWatcherRestartCountResetMilliseconds = defaultInternalSettings.getConfigWatcherRestartCountResetMilliseconds();
        this.connectionRefreshIntervalMillis = defaultInternalSettings.connectionRefreshIntervalMillis;
        this.retriesOnMalformedJSONduringVersioning = defaultInternalSettings.retriesOnMalformedJSONduringVersioning;
    }

    /**
     * Parameterized {@link InternalSettings} constructor
     *
     * @param maxConfigWatcherRestarts The maximum restart attempts for the {@link ConfigWatcher}, if it crashes.
     * @param configWatcherRestartCountResetMilliseconds The amount of milliseconds after which the restart counter for the {@link ConfigWatcher} gets reset.
     *                                                   Set to negative number to never reset the restart counter.
     */
    public InternalSettings(int maxConfigWatcherRestarts, int configWatcherRestartCountResetMilliseconds, long connectionRefreshIntervalMillis, int retriesOnMalformedJSONduringVersioning) {
        this.maxConfigWatcherRestarts = maxConfigWatcherRestarts;
        this.configWatcherRestartCountResetMilliseconds = configWatcherRestartCountResetMilliseconds;
        this.connectionRefreshIntervalMillis = connectionRefreshIntervalMillis;
        this.retriesOnMalformedJSONduringVersioning = retriesOnMalformedJSONduringVersioning;
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

    /**
     * @return The delay in milliseconds between each loop iteration in each connection.
     */
    public long getConnectionRefreshIntervalMillis() {
        return connectionRefreshIntervalMillis;
    }

    /**
     * @param connectionRefreshIntervalMillis The delay in milliseconds between each loop iteration in each connection.
     */
    public void setConnectionRefreshIntervalMillis(long connectionRefreshIntervalMillis) {
        this.connectionRefreshIntervalMillis = connectionRefreshIntervalMillis;
    }

    /**
     *
     * @return how many times the client is allowed to send falsy data to the server during the {@link net.jchad.server.model.networking.versioning versioning} process.
     */
    public int getRetriesOnMalformedJSONduringVersioning() {
        return retriesOnMalformedJSONduringVersioning;
    }

    /**
     *
     * @param retriesOnMalformedJSONduringVersioning how many times the client is allowed to send falsy data to the server during the {@link net.jchad.server.model.networking.versioning versioning} process.
     */
    public void setRetriesOnMalformedJSONduringVersioning(int retriesOnMalformedJSONduringVersioning) {
        this.retriesOnMalformedJSONduringVersioning = retriesOnMalformedJSONduringVersioning;
    }
}
