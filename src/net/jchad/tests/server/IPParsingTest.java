package net.jchad.tests.server;

import net.jchad.shared.networking.ip.IPAddress;
import net.jchad.shared.networking.ip.IPv4Address;
import net.jchad.shared.networking.ip.IPv6Address;
import net.jchad.shared.networking.ip.InvalidIPAddressException;

public class IPParsingTest {
    public static void main(String[] args) throws InvalidIPAddressException {
        System.out.println("IPv4 tests");
        testIPv4();

        System.out.println("IPv6 tests");
        testIPv6();

        System.out.println("IP tests");
        testIP();
    }


    public static void testIPv4() {
        assertTrue(IPv4Address.isValid("1.1.1.1"));
        assertTrue(IPv4Address.isValid("127.0.0.1"));
        assertTrue(IPv4Address.isValid("192.168.0.1"));
        assertTrue(IPv4Address.isValid("255.255.255.255"));
        assertFalse(IPv4Address.isValid("0"));
        assertFalse(IPv4Address.isValid("test"));
        assertFalse(IPv4Address.isValid("256.0.0.1"));
        assertFalse(IPv4Address.isValid("0.0"));
        assertFalse(IPv4Address.isValid("0:0:0:0"));
    }

    public static void testIPv6() {
        assertTrue(IPv6Address.isValid("0:0:0:0:0:0:0:0"));
        assertTrue(IPv6Address.isValid("::"));
        assertTrue(IPv6Address.isValid("AAAA:BBBB:CCCC:DDDD:EEEE:FFFF::"));
        assertTrue(IPv6Address.isValid("AAAA:BBBB:CCCC:DDDD:EEEE:FFFF:0:1234"));
        assertTrue(IPv6Address.isValid("AAAA:BBBB:CCCC:DDDD:EEEE:FFFF:10:1234"));
        assertFalse(IPv6Address.isValid(":"));
        assertFalse(IPv6Address.isValid("GHIJ::"));
        assertFalse(IPv6Address.isValid("AAAA::DDDD::0:1234"));
    }

    public static void testIP() throws InvalidIPAddressException {
        assertTrue(IPAddress.isValid("0:0:0:0:0:0:0:0"));
        assertTrue(IPAddress.isValid("::"));
        assertTrue(IPAddress.isValid("AAAA:BBBB:CCCC:DDDD:EEEE:FFFF::"));
        assertTrue(IPAddress.isValid("AAAA:BBBB:CCCC:DDDD:EEEE:FFFF:0:1234"));
        assertTrue(IPAddress.isValid("AAAA:BBBB:CCCC:DDDD:EEEE:FFFF:10:1234"));
        assertTrue(IPAddress.isValid("1.1.1.1"));
        assertTrue(IPAddress.isValid("127.0.0.1"));
        assertTrue(IPAddress.isValid("192.168.0.1"));
        assertTrue(IPAddress.isValid("255.255.255.255"));
        assertFalse(IPAddress.isValid("0"));
        assertFalse(IPAddress.isValid("test"));
        assertFalse(IPAddress.isValid("256.0.0.1"));
        assertFalse(IPAddress.isValid("0.0"));
        assertFalse(IPAddress.isValid("0:0:0:0"));
        assertFalse(IPAddress.isValid(":"));
        assertFalse(IPAddress.isValid("GHIJ::"));
        assertFalse(IPAddress.isValid("AAAA::DDDD::0:1234"));

        System.out.println();

        assertTrue(IPv4Address.fromString("0.0.0.0").equals(IPv4Address.fromString("0.0.0.0")));
        assertTrue(IPv6Address.fromString("::").equals(IPv6Address.fromString("0:0:0:0:0:0:0:0")));
        assertTrue(IPAddress.fromString("::").equals(IPAddress.fromString("0:0:0:0:0:0:0:0")));
        assertFalse(IPAddress.fromString("127.0.0.1").equals(IPAddress.fromString("0:0:0:0:0:0:0:0")));
    }

    private static void assertTrue(boolean condition) {
        if(condition) {
            System.out.println("Test case passed");
        } else {
            System.out.println("Test case failed");
        }
    }

    private static void assertFalse(boolean condition) {
        if(!condition) {
            System.out.println("Test case passed");
        } else {
            System.out.println("Test case failed");
        }
    }
}
