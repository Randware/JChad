package net.jchad.server.model.config.store.serverSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.jchad.server.model.networking.ip.IPAddress;

import java.util.ArrayList;

/**
 * Stores general configuration data the user is likely to change.
 */
public class ServerSettings {
    /**
     * Require password to connect.
     */
    private boolean requiresPassword;

    /**
     * Password required to connect.
     */
    private String password;

    /**
     * Only whitelisted IPs can connect.
     */
    private boolean whitelist;

    /**
     * Whitelisted IPs (stored in separate "whitelisted-ips.yml" file).
     */
    private ArrayList<IPAddress> whitelistedIPs;

    /**
     * Blacklisted IPs can't connect.
     */
    private boolean blacklist;

    /**
     * Blacklisted IPs (stored in separate "blacklisted-ips.yml").
     */
    private ArrayList<IPAddress> blacklistedIPs;

    /**
     * Hide usernames in all channels.
     */
    private boolean strictlyAnonymous;

    /**
     * Encrypt messages.
     */
    private boolean encrypted;

    /**
     * Port the server runs on.
     */
    private int port;

    /**
     * Default constructor for default configuration.
     */
    public ServerSettings() {
        ServerSettings defaultServerSettings = DefaultServerSettings.get();

        this.requiresPassword = defaultServerSettings.isRequiresPassword();
        this.password = defaultServerSettings.getPassword();
        this.whitelist = defaultServerSettings.isWhitelist();
        this.whitelistedIPs = defaultServerSettings.getWhitelistedIPs();
        this.blacklist = defaultServerSettings.isBlacklist();
        this.blacklistedIPs = defaultServerSettings.getBlacklistedIPs();
        this.strictlyAnonymous = defaultServerSettings.isStrictlyAnonymous();
        this.encrypted = defaultServerSettings.isEncrypted();
        this.port = defaultServerSettings.getPort();
    }

    /**
     * Constructor for config with custom values.
     *
     * @param requiresPassword Require password to connect
     * @param password  Password required to connect
     * @param whitelist Only whitelisted IPs are allowed to connect.
     * @param whitelistedIPs Whitelisted IPs
     * @param blacklist Blacklisted IPs are not allowed to connect.
     * @param blacklistedIPs Blacklisted IPs
     * @param strictlyAnonymous Hide usernames in all channels
     * @param encrypted Encrypt messages
     * @param port Port the server runs on.
     */
    public ServerSettings(boolean requiresPassword,
                          String password,
                          boolean whitelist,
                          ArrayList<IPAddress> whitelistedIPs,
                          boolean blacklist,
                          ArrayList<IPAddress> blacklistedIPs,
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

    @Override
    public String toString() {
        return "Config{" +
                "requiresPassword=" + requiresPassword +
                ", password='" + password + '\'' +
                ", whitelist=" + whitelist +
                ", whitelistedIPs=" + whitelistedIPs +
                ", blacklist=" + blacklist +
                ", blacklistedIPs=" + blacklistedIPs +
                ", strictlyAnonymous=" + strictlyAnonymous +
                ", encrypted=" + encrypted +
                ", port=" + port +
                '}';
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

    @JsonIgnore
    public ArrayList<IPAddress> getWhitelistedIPs() {
        return whitelistedIPs;
    }

    @JsonIgnore
    public void setWhitelistedIPs(ArrayList<IPAddress> whitelistedIPs) {
        this.whitelistedIPs = whitelistedIPs;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    @JsonIgnore
    public ArrayList<IPAddress> getBlacklistedIPs() {
        return blacklistedIPs;
    }

    @JsonIgnore
    public void setBlacklistedIPs(ArrayList<IPAddress> blacklistedIPs) {
        this.blacklistedIPs = blacklistedIPs;
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
