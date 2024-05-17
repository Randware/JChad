package net.jchad.server.model.config.store;

import net.jchad.shared.files.PathWatcher;
import net.jchad.shared.networking.packets.defaults.ServerInformationResponsePacket;

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
     * This determines how many times the client is allowed to send falsy data to the server.
     * If this limit gets exceeded, the connection will close.
     * If this gets set to a negative number, the default value (1) get used instead.
     */
    private int retriesOnInvalidPackets = 1;

    /**
     * This determines how many times the client is allowed to send the wrong password.
     * The connection will close if the client exceeds this number.
     * If this gets set to a negative number, the default value (3) get used instead.
     */
    private int retriesOnInvalidPassword = 3;

    /**
     * This regex checks if the wanted username (from the client) is valid.
     * It gives control on what the usernames should look like.
     * (Like allow underscores or other special characters...)
     *
     */
    private String usernameRegex = "^[A-Za-z]+(?:_[A-Za-z]+)?$";

    /**
     * This decries the regex above.
     * This explains the "criteria" for the username.
     * This gets sent to the client with the {@link ServerInformationResponsePacket ServerInformationPacket} and
     * when the client wants and invalid username.
     *
     */
    private String usernameRegexDescription = "The username is only allowed to have letters and one underscore in the middle of the name";


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
    public InternalSettings(int maxPathWatcherRestarts, int PathWatcherRestartCountResetMilliseconds,
                            long connectionRefreshIntervalMillis, int retriesOnInvalidPackets, int retriesOnInvalidPassword,
                            String usernameRegex, String usernameRegexDescription) {
        this.maxPathWatcherRestarts = maxPathWatcherRestarts;
        this.pathWatcherRestartCountResetMilliseconds = PathWatcherRestartCountResetMilliseconds;
        this.connectionRefreshIntervalMillis = connectionRefreshIntervalMillis;
        this.retriesOnInvalidPackets = retriesOnInvalidPackets;
        this.retriesOnInvalidPassword = retriesOnInvalidPassword;
        this.usernameRegex = usernameRegex;
        this.usernameRegexDescription = usernameRegexDescription;
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
     *         Returns the default value (100) if the configured one is below 0
     */
    public long getConnectionRefreshIntervalMillis() {
        if (connectionRefreshIntervalMillis < 0) return 100; //Default value
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
     *         Returns the default value (1) if the configured one is below 0
     */
    public int getRetriesOnInvalidPackets() {
        if (retriesOnInvalidPackets < 0) return 1;
        return retriesOnInvalidPackets;
    }

    /**
     *
     * @param retriesOnInvalidPackets how many times the client is allowed to send falsy data to the server process.
     */
    public void setRetriesOnInvalidPackets(int retriesOnInvalidPackets) {
        this.retriesOnInvalidPackets = retriesOnInvalidPackets;
    }

    /**
     *
     * @return the number that determines how many times the client is allowed to send the wrong password.
     */
    public int getRetriesOnInvalidPassword() {
        if(retriesOnInvalidPassword < 0) return 3;
        return retriesOnInvalidPassword;
    }

    /**
     *
     * @param retriesOnInvalidPassword the number that determines how many times the client is allowed to send the wrong password.
     */
    public void setRetriesOnInvalidPassword(int retriesOnInvalidPassword) {
        this.retriesOnInvalidPassword = retriesOnInvalidPassword;
    }

    /**
     *
     * @return the regex that validates all usernames
     */
    public String getUsernameRegex() {
        return usernameRegex;
    }

    /**
     *
     * @param usernameRegex the regex that validates all usernames
     */
    public void setUsernameRegex(String usernameRegex) {
        this.usernameRegex = usernameRegex;
    }

    /**
     *
     * @return the "criteria" for usernames described in readable words
     */
    public String getUsernameRegexDescription() {
        return usernameRegexDescription;
    }

    /**
     *
     * @param usernameRegexDescription sets the "criteria" for usernames described in readable words
     */
    public void setUsernameRegexDescription(String usernameRegexDescription) {
        this.usernameRegexDescription = usernameRegexDescription;
    }


}
