package net.jchad.server.model.server.util;

import com.google.gson.JsonSyntaxException;
import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.*;

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
    <T extends Packet> T readJSON(Class<T> returningClassType, PacketType reuiredPacketType) {
     return readJSON(returningClassType, reuiredPacketType, this.retries);
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
    <T extends Packet> T readJSON(Class<T> returningClassType, PacketType reuiredPacketType, int retries) {
        T returningObject = null;
        for (int failedAttempts = 0; retries >= failedAttempts; failedAttempts++) {
            try {
                Thread.currentThread().sleep(sleepInterval);
                returningObject = serverThread.getGson().fromJson(serverThread.getScanner().next(), returningClassType);
                if (!returningObject.isValid()) {
                    throw new InvalidPacketException("The received packet is not valid");
                } else {
                    break;
                }
            } catch ( JsonSyntaxException | InvalidPacketException e) {
                if (failedAttempts >= retries) {
                    serverThread.getMessageHandler().handleDebug("%s sent an invalid packet. The connection get terminated now!".formatted(serverThread.getRemoteAddress()));
                    serverThread.close("%s sent to many invalid packets".formatted(serverThread.getRemoteAddress()));
                    break;
                } else {
                    serverThread.getMessageHandler().handleDebug("%s sent an invalid packet. The connection gets terminated if the server receives %d more invalid packet(s)".formatted(serverThread.getRemoteAddress(), retries - failedAttempts));
                    writeJSON(new InvalidPacket(reuiredPacketType, "The provided packet was not valid").toJSON());
                }
            } catch (InterruptedException e) {
                    serverThread.getMessageHandler().handleError(new Exception("The client thread connected with %s got interrupted unsuspectingly".formatted(serverThread.getRemoteAddress()), e));
                    serverThread.close("Thead got interrupted unsuspectingly");
            }
        }


        return returningObject;
    }


    <T extends String> void writeJSON(T json) {
            serverThread.getPrintWriter().println(json);
            serverThread.getPrintWriter().flush();
    }

    ServerThread getServerThread() {
        return serverThread;
    }

    int getRetries() {
        return retries;
    }

    long getSleepInterval() {
        return sleepInterval;
    }
}