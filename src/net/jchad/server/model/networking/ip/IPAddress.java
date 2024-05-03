package net.jchad.server.model.networking.ip;

import java.net.InetAddress;

public interface IPAddress {
    static IPAddress fromString(String ipAddress) throws InvalidIPAddressException {
        try {
            return IPv4Address.fromString(ipAddress);
        } catch (InvalidIPAddressException ignore){}

        try {
            return IPv6Address.fromString(ipAddress);
        } catch (InvalidIPAddressException ignore){}

        throw new InvalidIPAddressException();
    }
    static IPAddress fromInetAddress(InetAddress ipAddress) throws InvalidIPAddressException {
        return fromString(ipAddress.getHostAddress());
    }
    String getString();

    static boolean isValid(String ipAddress) {
        return IPv4Address.isValid(ipAddress) || IPv6Address.isValid(ipAddress);
    }


}
