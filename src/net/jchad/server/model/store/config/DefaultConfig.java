package net.jchad.server.model.store.config;

import java.util.ArrayList;

// Defines default config values and creates configs with them
public class DefaultConfig {
    // Require password to connect
    private static final boolean requiresPassword = false;

    // Password required to connect
    private static final String password = "";

    // Only whitelisted IPs can connect (stored in whitelist.yml)
    private static final boolean whitelist = false;

    // List of whitelisted IPs
    private static final ArrayList<String> whitelistedIPs = new ArrayList<>();

    // Blacklisted IPs can't connect (stored in blacklist.yml)
    private static final boolean blacklist = false;

    // List of blacklisted
    private static final ArrayList<String> blacklistedIPs = new ArrayList<>();

    // Hide usernames in all channels
    private static final boolean strictlyAnonymous = false;

    // Encrypt messages
    private static final boolean encrypted = true;

    // Server port
    private static final int port = 13814;

    // Create a Config instance with the specified default values
    public static Config get() {
        return new Config(
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
