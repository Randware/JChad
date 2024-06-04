package net.jchad.server.model.server;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This Class aims to prevent <b><u>Dead Threads</u></b>, by loop through every connection and checking if the Socket is closed.
 * <p><i>What is a Dead Thread?</i></p>
 * <p>A <b>DEAD THREAD</b> is a thread that runs normally, except it has no opened connection associated</p>
 * <p>This happens when the client does not close the connection properly</p>
 */
public class ConnectionCheckerThread implements Runnable {
    private final MainSocket mainSocket;

    public ConnectionCheckerThread(MainSocket mainSocket) {
        this.mainSocket = mainSocket;
    }


    //TODO Implement proper Dead Thread detection
    @Override
    public void run() {
        mainSocket.getMessageHandler().handleDebug("Connection checking process started");
        while (true) {
            try {
            mainSocket.getServerThreadSet().forEach(connection -> {
                if (connection.getSocket().isClosed()) {
                    mainSocket.getMessageHandler().handleDebug("Disconnected thread detected (" + connection.getRemoteAddress() + ")");
                    connection.close("Dead Thread detected", false);
                }
            });

                TimeUnit.SECONDS.sleep(mainSocket.getServer().getConfig().getInternalSettings().getConnectionCheckerThreadSleepTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


}
