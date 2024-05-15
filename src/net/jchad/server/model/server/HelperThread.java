package net.jchad.server.model.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import net.jchad.shared.networking.packets.*;

import java.io.IOException;
import java.util.function.Predicate;

public abstract class HelperThread{
    private final ServerThread serverThread;
    private final int retries;
    private final long sleepInterval;

    public HelperThread(ServerThread serverThread) {
        if (serverThread == null) {
            throw new NullPointerException("The serverThread can not be null");
        }
        this.serverThread = serverThread;
        this.retries = serverThread.getServer().getConfig().getInternalSettings().getRetriesOnInvalidPackets();
        this.sleepInterval = serverThread.getServer().getConfig().getInternalSettings().getConnectionRefreshIntervalMillis();
    }


    /**
     * <p>This methode reads the JSON in the {@link net.jchad.server.model.server.ServerThread ServerThread} using the {@link com.google.gson.stream.JsonReader JsonReader}</p>
     * <p>It may occur that this methode returns null BUT this is rare. If the methode returns null just close the {@link net.jchad.server.model.server.ServerThread ServerThread} </p>
     *
     * @param returningClassType The object which should get returned wi
     * @param reuiredPacketType The required packet type that gets sent to the client when he sends an invalid packet.
     *                          This does not affect the methode in any way. It just tells the client which packet type it (the client) should have sent.
     * @return T
     * @param <T>
     */
    public <T extends Packet> T readJSON(Class<T> returningClassType, PacketType reuiredPacketType) {
        T returningObject = null;
        boolean skip = false;
        for (int failedAttempts = 1; retries >= failedAttempts; failedAttempts++) {
            try {
                while (serverThread.getJsonReader().hasNext()) {
                    Thread.currentThread().sleep(sleepInterval);
                    if (skip) {
                        serverThread.getJsonReader().skipValue();
                        skip = false;
                    }
                    JsonToken jsToken = serverThread.getJsonReader().peek();
                    if (jsToken.equals(JsonToken.BEGIN_OBJECT)) {
                        returningObject = serverThread.getMainSocket().getGson().fromJson(serverThread.getJsonReader(), returningClassType);
                        if (!returningObject.isValid()) {
                            returningObject = null;
                            throw new InvalidPacketException("The received Packet is not valid.");
                        }

                    } else {
                        skip = true;
                        throw new InvalidPacketException("Invalid JSON while trying to read object");
                    }

                }
            } catch (MalformedJsonException | JsonSyntaxException | InvalidPacketException e) {
                if (failedAttempts >= retries) {
                    serverThread.getMessageHandler().handleDebug("%s sent an invalid packet. The connection get terminated after %d failed attempts from %d maximum tries".formatted(serverThread.getRemoteAddress(), failedAttempts, retries));
                    serverThread.close("%s sent to many invalid packets".formatted(serverThread.getRemoteAddress()));
                    break;
                } else {
                    serverThread.getMessageHandler().handleDebug("%s sent an invalid packet! The connection gets terminated if the server receives %d more invalid packets".formatted(serverThread.getRemoteAddress(), retries - failedAttempts));
                    writeJSON(new InvalidPacket(reuiredPacketType, "The provided packet was not valid").toJSON());
                }
            }
            catch (IOException ioe) {
                serverThread.getMessageHandler().handleDebug(new IOException("An IO exception occurred! ", ioe));
                serverThread.close("An IO exception occurred while trying to read JSON");
                break;
            } catch (InterruptedException e) {
                serverThread.getMessageHandler().handleDebug("Thread for %s got interrupted unexpectedly!".formatted(serverThread.getRemoteAddress()));
                serverThread.close("The Thread got interrupted unexpectedly");
            }
        }

        if (returningObject == null) {
            serverThread.close("The JSON reading process failed");
        }

            return returningObject;
    }


    public <T extends String> void writeJSON(T json) {
            serverThread.getPrintWriter().println(json);
            serverThread.getPrintWriter().flush();
    }

    public ServerThread getServerThread() {
        return serverThread;
    }

    int getRetries() {
        return retries;
    }

    long getSleepInterval() {
        return sleepInterval;
    }
}
