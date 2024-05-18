package net.jchad.client.model.client.connection;

/**
 * This class will be run on a separate thread and read all packets being sent by the server.
 * Once a package is successfully read, this class will supply its holder class with the packet
 * using a callback. Since the packet maybe encrypted and this class should not handle decryption
 * for performance reasons, the package this class returns still needs to possibly be decrypted and
 * deserialized.
 */
public class ConnectionReader {
}
