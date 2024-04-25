package net.jchad.server.model.config.store.serverSettings;

import net.jchad.server.model.networking.ip.IPAddress;

import java.util.ArrayList;

/**
 * Defines default configuration values.
 */
public class DefaultServerSettings {
    /**
     * Require password to connect.
     */
    private static final boolean requiresPassword = false;

    /**
     * Password required to connect.
     */
    private static final String password = "";

    /**
     * Only whitelisted IPs can connect.
     */
    private static final boolean whitelist = false;

    /**
     * Whitelisted IPs (stored in separate "whitelisted-ips.yml" file.
     */
    private static final ArrayList<IPAddress> whitelistedIPs = new ArrayList<>();

    /**
     * Blacklisted IPs can't connect.
     */
    private static final boolean blacklist = false;

    /**
     * Blacklisted IPs (stored in separate "blacklisted-ips.yml").
     */
    private static final ArrayList<IPAddress> blacklistedIPs = new ArrayList<>();

    /**
     * Hide usernames in all channels.
     */
    private static final boolean strictlyAnonymous = false;

    /**
     * Encrypt messages.
     */
    private static final boolean encrypted = true;

    /**
     * Port the server runs on.
     */
    private static final int port = 13814;

    /**
     * Creates a config with the specified default values.
     *
     * @return {@link ServerSettings} with the specified default values.
     */
    public static ServerSettings get() {
        return new ServerSettings(
                requiresPassword,
                password,
                whitelist,
                whitelistedIPs,
                blacklist,
                blacklistedIPs,
                strictlyAnonymous,
                encrypted,
                port
        );
    }
}
