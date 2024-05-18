package net.jchad.client.model.client.connection;

/**
 * This class is responsible for sending packets to the server. Because this class
 * should not handle the encryption process for performance reasons, it will only send
 * the serialized packet string to the server, not serialize it itself.
 */
public class ConnectionWriter {
}
