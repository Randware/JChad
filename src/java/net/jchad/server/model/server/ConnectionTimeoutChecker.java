package net.jchad.server.model.server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This Class adds timeouts by iterating through each connection and checking if it
 * exceeds the configured timeouts
 */
public class ConnectionTimeoutChecker implements Runnable {
    private final MainSocket mainSocket;
    private Set<ServerThread> serverThreadSet = null;

    public ConnectionTimeoutChecker(MainSocket mainSocket) {
        this.mainSocket = mainSocket;
    }


    //TODO Implement proper Dead Thread detection
    @Override
    public void run() {
        try {
            serverThreadSet = mainSocket.getServerThreadSet();
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(Math.min(mainSocket.getServer().getConfig().getInternalSettings().getMainTimeout(), mainSocket.getServer().getConfig().getInternalSettings().getHandshakeTimeout()));
                } catch (InterruptedException ignore) {
                    //Ignore
                }
            }
        } catch (Exception e) {
            mainSocket.getMessageHandler().handleError(new Exception("An error occurred in the ConnectionTimeoutChecker. The ConnectionTimeoutChecker gets shut down now", e));
            Thread.currentThread().interrupt();
        }
    }


}
