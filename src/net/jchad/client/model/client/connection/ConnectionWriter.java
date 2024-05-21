package net.jchad.client.model.client.connection;

import net.jchad.shared.networking.packets.Packet;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * This class is responsible for sending packets to the server. Because this class
 * should not handle the encryption process for performance reasons, it will only send
 * the serialized packet string to the server, not serialize it itself.
 */
public class ConnectionWriter extends Thread {
    /**
     * This PrintWriter wraps the {@link OutputStream} provided on construction of this object.
     */
    private PrintWriter out;

    public ConnectionWriter(OutputStream out) {
        this.out = new PrintWriter(out, true);

        start();
    }

    /**
     * This method will write a serialized and possibly encrypted {@link Packet} string
     * into the {@link OutputStream} previously provided to this ConnectionWriter.
     *
     * @param packetString the serialized and possibly encrypted {@link Packet} string.
     */
    public void writePacketString(String packetString) {
        out.println(packetString);
    }
}
