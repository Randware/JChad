package net.jchad.client.model.client.connection;

import net.jchad.client.model.client.packets.PacketHandler;
import net.jchad.server.model.server.ConnectionClosedException;

import java.io.*;
import java.util.function.Consumer;

/**
 * This class will be run on a separate thread and read all packets being sent by the server.
 * Once a package is successfully read, this class will supply its holder class with the packet
 * using a callback. Since the packet maybe encrypted and this class should not handle decryption
 * for performance reasons, the package this class returns still needs to possibly be decrypted and
 * deserialized.
 */
public class ConnectionReader extends Thread implements AutoCloseable {
    /**
     * This PrintWriter wraps the {@link InputStream} provided on construction of this object.
     */
    private BufferedReader in;

    /**
     * This packet handler will be called when a new packet string is read
     * or an error occurs during the reading of data.
     */
    private PacketHandler handler;

    /**
     * Define if the reader should be reading.
     */
    private boolean reading;

    /**
     * This class runs on a separate thread and reads string from the provided {@link InputStream}.
     * When it successfully reads a string it will call the packetCallback, if it runs into an error
     * it will call the errorCallback.
     *
     * @param in the {@link InputStream} that should be read.
     * @param handler the {@link PacketHandler} that will be provided with packet strings and
     *                notified when an error occurs.
     */
    public ConnectionReader(InputStream in, PacketHandler handler) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.handler = handler;
        this.reading = true;
        start();
    }

    @Override
    public void run() {
        try {
            read();
        } catch (IOException e) {
            if(handler != null) {
                handler.handlePacketReaderError(e);
            }
        }
    }

    /**
     * Starts the reading process. If a string was successfully read it will be provided to
     * this classes holder using the packetCallback.
     *
     * @throws IOException if something goes wrong when reading input.
     */
    private void read() throws IOException {
        String read = in.readLine();

        if(read != null) {
            handler.handlePacketString(read);
        } else {
            handler.handlePacketReaderError(new ClosedConnectionException("Connection closed"));
        }
    }

    @Override
    public void close() throws Exception {
        in.close();
    }
}
