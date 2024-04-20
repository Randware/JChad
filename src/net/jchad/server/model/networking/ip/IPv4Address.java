package net.jchad.server.model.networking.ip;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

public class IPv4Address implements IPAddress {
    private final String ipString;

    private IPv4Address(String ipString) {
        this.ipString = ipString;
    }

    public static IPAddress fromString(String ipString) throws InvalidIPAddressException {
        if(IPv4Address.isValid(ipString)) {
            try {
                return new IPv4Address(Arrays.toString(Inet4Address.getByName(ipString).getAddress()));
            } catch (UnknownHostException e) {
                throw new InvalidIPAddressException();
            }
        } else {
            throw new InvalidIPAddressException();
        }
    }

    @Override
    public String getString() {
        return ipString;
    }

    public static boolean isValid(String ipAddress) {
        return ipAddress.matches("\\b(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPv4Address that = (IPv4Address) o;
        return Objects.equals(ipString, that.ipString);
    }
}
