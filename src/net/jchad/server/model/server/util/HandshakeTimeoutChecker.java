package net.jchad.server.model.server.util;

import net.jchad.server.model.server.MainSocket;
import net.jchad.server.model.server.ServerThread;

import java.util.HashMap;

/**
 * This class timeout {@link ServerThread connections} if the <b>set handshake-timeout</b> gets <b>exceeded</b>
 */
public class HandshakeTimeoutChecker implements Runnable {
    private final MainSocket mainSocket;
    private final HashMap<ServerThread, Long> timestamps = new HashMap<>();

    public HandshakeTimeoutChecker(MainSocket mainSocket) {
        this.mainSocket = mainSocket;
    }



    @Override
    public void run() {
        mainSocket.getMessageHandler().handleDebug("Connection checking process started");
        while (!Thread.interrupted()) {
        try {
                long timeout = mainSocket.getServer().getConfig().getInternalSettings().getHandshakeTimeout();
            mainSocket.getServerThreadSet().forEach(connection -> {
                if (!timestamps.containsKey(connection) && (connection.getUser() == null || !connection.getUser().isReadyToReceiveMessages())) {
                    timestamps.put(connection, System.currentTimeMillis());
                }
                if ( connection.getUser() == null || !connection.getUser().isReadyToReceiveMessages()) {
                        if (System.currentTimeMillis() >= timestamps.get(connection) + timeout) {
                            mainSocket.getMessageHandler().handleDebug("Connection Timed out during the handshake with (" + connection.getRemoteAddress() + ")");
                            connection.close("Timed out during the handshake", false);
                            timestamps.remove(connection);
                        }
                } else {
                    timestamps.remove(connection);
                }

            });

           Thread.sleep(mainSocket.getServer().getConfig().getInternalSettings().getTimeoutCheckerRefreshIntervalMillis());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        }
    }


}