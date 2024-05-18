package net.jchad.client.model.store.connection;

/**
 * This class will collect all data that may be required to connect to a server.
 * An instance of this class needs to be passed to the ServerConnector to initialize
 * the connection process.
 */
public class ConnectionDetails {
    private String host;
    private int port;
    private String username;
    private String password;

    public ConnectionDetails(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
