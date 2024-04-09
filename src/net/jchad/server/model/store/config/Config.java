package net.jchad.server.model.store.config;

import java.util.ArrayList;

public class Config {
    // Require password to connect
    private boolean requiresPassword;

    // Password required to connect
    private String password;

    // Only whitelisted IPs can connect (stored in whitelist.yml)
    private boolean whitelist;

    // List of whitelisted IPs
    private ArrayList<String> whitelistedIPs;

    // Blacklisted IPs can't connect (stored in blacklist.yml)
    private boolean blacklist;

    // List of blacklisted
    private ArrayList<String> blacklistedIPs;

    // Hide usernames in all channels
    private boolean strictlyAnonymous;

    // Encrypt messages
    private boolean encrypted;

    // Server port
    private int port;

    public Config(boolean requiresPassword,
                  String password,
                  boolean whitelist,
                  ArrayList<String> whitelistedIPs,
                  boolean blacklist,
                  ArrayList<String> blacklistedIPs,
                  boolean strictlyAnonymous,
                  boolean encrypted,
                  int port) {

        this.requiresPassword = requiresPassword;
        this.password = password;
        this.whitelist = whitelist;
        this.whitelistedIPs = whitelistedIPs;
        this.blacklist = blacklist;
        this.blacklistedIPs = blacklistedIPs;
        this.strictlyAnonymous = strictlyAnonymous;
        this.encrypted = encrypted;
        this.port = port;
    }

    public boolean isRequiresPassword() {
        return requiresPassword;
    }

    public void setRequiresPassword(boolean requiresPassword) {
        this.requiresPassword = requiresPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    public boolean isStrictlyAnonymous() {
        return strictlyAnonymous;
    }

    public void setStrictlyAnonymous(boolean strictlyAnonymous) {
        this.strictlyAnonymous = strictlyAnonymous;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
