package net.jchad.client.model.client.connection;

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
     * This callback will be called when the reader has read a new packet string.
     */
    private Consumer<String> packetCallback;

    /**
     * This callback will be called when the runs into an error.
     */
    private Consumer<Exception> errorCallback;

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
     * @param packetCallback the callback that will be provided with the packet string.
     * @param errorCallback the callback that will be called if an error occurs.
     */
    public ConnectionReader(InputStream in, Consumer<String> packetCallback, Consumer<Exception> errorCallback) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.packetCallback = packetCallback;
        this.errorCallback = errorCallback;
        this.reading = true;
    }

    @Override
    public void run() {
        try {
            read();
        } catch (IOException e) {
            errorCallback.accept(e);
        }
    }

    /**
     * Starts the reading process. If a string was successfully read it will be provided to
     * this classes holder using the packetCallback.
     *
     * @throws IOException if something goes wrong when reading input.
     */
    private void read() throws IOException {
        while(reading) {
            if(packetCallback != null) {
                packetCallback.accept(in.readLine());
            }
        }
    }
}
