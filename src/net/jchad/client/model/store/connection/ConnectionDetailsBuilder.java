package net.jchad.client.model.store.connection;

/**
 * This class is a builder for the {@link ConnectionDetails} class.
 */
public class ConnectionDetailsBuilder {
    private final ConnectionDetails details;

    public ConnectionDetailsBuilder() {
        details = new ConnectionDetails(null, null, 0, null, null);
    }

    public ConnectionDetailsBuilder addConnectionName(String connectionName) {
        details.setConnectionName(connectionName);

        return this;
    }

    public ConnectionDetailsBuilder addHost(String host) {
        details.setHost(host);

        return this;
    }

    public ConnectionDetailsBuilder addPort(int port) {
        details.setPort(port);

        return this;
    }

    public ConnectionDetailsBuilder addUsername(String username) {
        details.setUsername(username);

        return this;
    }

    public ConnectionDetailsBuilder addPassword(String password) {
        details.setPassword(password);

        return this;
    }

    public ConnectionDetails build() {
        return details;
    }
}
