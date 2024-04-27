package net.jchad.server.model.networking.versioning;

import java.util.Arrays;

public class ClientVersionMatcherPacket {

    private final double preferredVersion;
    private final double[] availableVersions;

    public ClientVersionMatcherPacket(double preferredVersion, double[] availableVersions) {
        this.preferredVersion = preferredVersion;
        this.availableVersions = availableVersions;
    }

    public double getPreferredVersion() {
        return preferredVersion;
    }

    public double[] getAvailableVersions() {
        return availableVersions;
    }

    @Override
    public String toString() {
        return "preferredVersion: " + preferredVersion + ", availableVersions: " + Arrays.toString(availableVersions);
    }
}
