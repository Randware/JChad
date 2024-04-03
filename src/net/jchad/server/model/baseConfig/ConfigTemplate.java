package net.jchad.server.model.baseConfig;

import java.util.ArrayList;

public class ConfigTemplate {

    private String version;

    private boolean serverRequiresPassword;

    private String password = "";

    private boolean whitelist = false;

    private ArrayList<String> whitelistedIPs;

    private boolean blacklist;

    private ArrayList<String> blacklistedIPs;

    private boolean remoteAdmin;

    //private ArrayList<>
    //TODO implement user-ids and their passwords and admin keys/users...

    private boolean strictlyAnonymous;

    private boolean strictlyEncrypt;

    //Default port for JChad:
    // (Get the alphabetical index of the letters used in JChad)
    // J-> 10 + C -> 3 + H -> 8 + A -> 1 + D -> 4 = 103814 (To make it a valid port trim the "0") => 13814
    // 13814
    private int port;

    /**
     * Constructor for the config-template
     *
     * @param serverRequiresPassword specifies if the server requires a password
     * @param password specifies what tha password should be  (value gets ignored if {@code serverRequiresPassword} is set to false)
     * @param whitelist specifies if the whitelist is enabled
     * @param whitelistedIPs specifies what ips should be whitelisted (value gets ignored if {@code whitelist} is set to false)
     * @param blacklist specifies if the blacklist is enabled (for like banned ips)
     * @param blacklistedIPs specifies what ips should be blacklisted/banned (value gets ignored if {@code blacklist} is set to false)
     * @param strictlyAnonymous specifies if all users across all chat rooms should be enforced to be anonymous
     * @param strictlyEncrypt specifies if all messages sent across all chat rooms should be enforced to be encrypted
     * @param port specifies the port (I highly recommend using the default port 13814)
     */

    public ConfigTemplate(boolean serverRequiresPassword, String password, boolean whitelist,
                          ArrayList<String> whitelistedIPs, boolean blacklist,
                          ArrayList<String> blacklistedIPs, boolean strictlyAnonymous,
                          boolean strictlyEncrypt, int port) {

        setServerRequiresPassword(serverRequiresPassword);
        setPassword(password);
        setWhitelist(whitelist);
        setWhitelistedIPs(whitelistedIPs);
        setBlacklist(blacklist);
        setBlacklistedIPs(blacklistedIPs);
        setStrictlyAnonymous(strictlyAnonymous);
        setStrictlyEncrypt(strictlyEncrypt);
        setPort(port);

    }

    /**
     * {@inheritDoc}
     */
    public ConfigTemplate(boolean serverRequiresPassword, String password, boolean whitelist,
                          ArrayList<String> whitelistedIPs, boolean blacklist,
                          ArrayList<String> blacklistedIPs, boolean strictlyAnonymous,
                          boolean strictlyEncrypt) {

        this(serverRequiresPassword, password, whitelist,
             whitelistedIPs, blacklist, blacklistedIPs,
             strictlyAnonymous, strictlyEncrypt, 13814);

    }

    public boolean isServerRequiresPassword() {
        return serverRequiresPassword;
    }

    public void setServerRequiresPassword(boolean serverRequiresPassword) {
        this.serverRequiresPassword = serverRequiresPassword;
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

    public ArrayList<String> getWhitelistedIPs() {
        return whitelistedIPs;
    }

    public void setWhitelistedIPs(ArrayList<String> whitelistedIPs) {
        this.whitelistedIPs = whitelistedIPs;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    public ArrayList<String> getBlacklistedIPs() {
        return blacklistedIPs;
    }

    public void setBlacklistedIPs(ArrayList<String> blacklistedIPs) {
        this.blacklistedIPs = blacklistedIPs;
    }

    public boolean isStrictlyAnonymous() {
        return strictlyAnonymous;
    }

    public void setStrictlyAnonymous(boolean strictlyAnonymous) {
        this.strictlyAnonymous = strictlyAnonymous;
    }

    public boolean isStrictlyEncrypt() {
        return strictlyEncrypt;
    }

    public void setStrictlyEncrypt(boolean strictlyEncrypt) {
        this.strictlyEncrypt = strictlyEncrypt;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
