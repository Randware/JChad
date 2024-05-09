package net.jchad.server.model.config.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.jchad.shared.networking.ip.IPAddress;

import java.util.ArrayList;

/**
 * Stores general configuration data the user is likely to change.
 */
public class ServerSettings {
    /**
     * Require password to connect.
     */
    private boolean requiresPassword = false;

    /**
     * Password required to connect.
     */
    private String password = "";

    /**
     * Only whitelisted IPs can connect.
     */
    private boolean whitelist = false;

    /**
     * Whitelisted IPs (stored in separate "whitelisted-ips.yml" file).
     */
    private ArrayList<IPAddress> whitelistedIPs = new ArrayList<>();

    /**
     * Blacklisted IPs can't connect.
     */
    private boolean blacklist = false;

    /**
     * Blacklisted IPs (stored in separate "blacklisted-ips.yml").
     */
    private ArrayList<IPAddress> blacklistedIPs = new ArrayList<>();

    /**
     * Hide usernames in all channels.
     */
    private boolean strictlyAnonymous = false;

    /**
     * Encrypt messages.
     */
    private boolean encryptMessages = true;

    /**
     * Encrypt communications
     */
    private boolean encryptCommunications = false;

    /**
     * Port the server runs on.
     */
    private int port = 13814;

    /**
     * Default constructor for default configuration.
     */
    public ServerSettings() {

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
     * @param encryptMessages Encrypt messages
     * @param port Port the server runs on.
     */
    public ServerSettings(boolean requiresPassword,
                          String password,
                          boolean whitelist,
                          ArrayList<IPAddress> whitelistedIPs,
                          boolean blacklist,
                          ArrayList<IPAddress> blacklistedIPs,
                          boolean strictlyAnonymous,
                          boolean encryptMessages,
                          boolean encryptCommunications,
                          int port) {

        this.requiresPassword = requiresPassword;
        this.password = password;
        this.whitelist = whitelist;
        this.whitelistedIPs = whitelistedIPs;
        this.blacklist = blacklist;
        this.blacklistedIPs = blacklistedIPs;
        this.strictlyAnonymous = strictlyAnonymous;
        this.encryptMessages = encryptMessages;
        this.encryptCommunications = encryptCommunications;
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
                ", encryptMessages=" + encryptMessages +
                ", encryptCommunication=" + encryptCommunications +
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

    public boolean isEncryptMessages() {
        return encryptMessages;
    }

    public void setEncryptMessages(boolean encryptMessages) {
        this.encryptMessages = encryptMessages;
    }

    public boolean isEncryptedMessages() {return encryptCommunications;}

    public void setEncryptCommunications(boolean encryptCommunications) {this.encryptCommunications = encryptCommunications;}

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
