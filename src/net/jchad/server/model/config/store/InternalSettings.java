package net.jchad.server.model.config.store;

import net.jchad.shared.files.PathWatcher;

/**
 * Stores internal settings the server uses to function.
 * These values have sensible defaults, but if the user wants to modify them, they can.
 */
public class InternalSettings {
    /**
     * The maximum restart attempts for the {@link PathWatcher}, if it crashes.
     */
    private int maxPathWatcherRestarts = 3;

    /**
     * The amount of milliseconds after which the restart counter for the {@link PathWatcher} gets reset.
     * Set to negative number to never reset the restart counter.
     */
    private int pathWatcherRestartCountResetMilliseconds = 5000;

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
    private long connectionRefreshIntervalMillis = 100;

    /**
     * This determines how many times the client is allowed to send falsy data to the server .
     * If this limit gets exceeded the connection will close.
     * If this gets set to a negative number the default value (1) get used instead.
     */
    private int retriesOnInvalidPackets = 1;

    /**
     * Default {@link InternalSettings} constructor
     */
    public InternalSettings() {
            }

    /**
     * Parameterized {@link InternalSettings} constructor
     *
     * @param maxPathWatcherRestarts The maximum restart attempts for the {@link PathWatcher}, if it crashes.
     * @param PathWatcherRestartCountResetMilliseconds The amount of milliseconds after which the restart counter for the {@link PathWatcher} gets reset.
     *                                                   Set to negative number to never reset the restart counter.
     */
    public InternalSettings(int maxPathWatcherRestarts, int PathWatcherRestartCountResetMilliseconds, long connectionRefreshIntervalMillis, int retriesOnInvalidPackets) {
        this.maxPathWatcherRestarts = maxPathWatcherRestarts;
        this.pathWatcherRestartCountResetMilliseconds = PathWatcherRestartCountResetMilliseconds;
        this.connectionRefreshIntervalMillis = connectionRefreshIntervalMillis;
        this.retriesOnInvalidPackets = retriesOnInvalidPackets;
    }

    /**
     * @return The maximum restart attempts for the {@link PathWatcher}, if it crashes.
     */
    public int getMaxPathWatcherRestarts() {
        return maxPathWatcherRestarts;
    }

    /**
     * @param maxPathWatcherRestarts The maximum restart attempts for the {@link PathWatcher}, if it crashes.
     */
    public void setMaxPathWatcherRestarts(int maxPathWatcherRestarts) {
        this.maxPathWatcherRestarts = maxPathWatcherRestarts;
    }

    /**
     * @return The amount of milliseconds after which the restart counter for the {@link PathWatcher} gets reset.
     */
    public int getPathWatcherRestartCountResetMilliseconds() {
        return pathWatcherRestartCountResetMilliseconds;
    }

    /**
     * @param PathWatcherRestartCountResetMilliseconds The amount of milliseconds after which the restart counter for the {@link PathWatcher} gets reset.
     *                                                   Set to negative number to never reset the restart counter.
     */
    public void setPathWatcherRestartCountResetMilliseconds(int PathWatcherRestartCountResetMilliseconds) {
        this.pathWatcherRestartCountResetMilliseconds = PathWatcherRestartCountResetMilliseconds;
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
     * @return how many times the client is allowed to send falsy data to the server.
     */
    public int getRetriesOnInvalidPackets() {
        return retriesOnInvalidPackets;
    }

    /**
     *
     * @param retriesOnInvalidPackets how many times the client is allowed to send falsy data to the server process.
     */
    public void setRetriesOnInvalidPackets(int retriesOnInvalidPackets) {
        this.retriesOnInvalidPackets = retriesOnInvalidPackets;
    }
}
