package net.jchad.client.model.client.connection;


import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;

import java.io.*;
import java.util.function.Consumer;

/**
 * This class will be run on a separate thread and read all packets being sent by the server.
 * Once a package is successfully read, this class will supply its holder class with the packet
 * using a callback. Since the packet maybe encrypted and this class should not handle decryption
 * for performance reasons, the package this class returns still needs to possibly be decrypted and
 * deserialized.
 */
public class ConnectionReader extends Thread {
    /**
     * This PrintWriter wraps the {@link InputStream} provided on construction of this object.
     */
    private BufferedReader in;

    /**
     * This class runs on a separate thread and reads string from the provided {@link InputStream}.
     * When it successfully reads a string it will call the packetCallback, if it runs into an error
     * it will call the errorCallback.
     *
     * @param in the {@link InputStream} that should be read.
     */
    public ConnectionReader(InputStream in) {
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    /**
     * Reads and returns the next line from the stream.
     *
     * @return the read string, null if the connection was closed by the server.
     * @throws IOException if something goes wrong when reading input.
     * @throws ClosedConnectionException if the server closes the connection.
     */
    public String read() throws IOException, ClosedConnectionException {
        String read = in.readLine();

        if(read != null) {
            return read;
        } else {
            throw new ClosedConnectionException("Connection was closed by the server");
        }
    }

    public <T extends Packet> T readPacket() throws InvalidPacketException, IOException, ClosedConnectionException {
        String read = read();
        Packet packet = Packet.fromJSON(read);
        if (packet != null && packet.isValid()) {
            if (packet.getClass().equals(ConnectionClosedPacket.class)){
                ConnectionClosedPacket closedPacket = (ConnectionClosedPacket) packet;
                throw new ClosedConnectionException(closedPacket.getMessage());
            } else {
                return (T) packet;
            }
        } else {
            throw new InvalidPacketException("The packet that was read is invalid");
        }
    }

    public void close() throws IOException {
        in.close();
    }
}
