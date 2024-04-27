package net.jchad.server.model.server;

import net.jchad.server.model.error.MessageHandler;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class TempTest implements MessageHandler {
    public static void main(String[] args) {
        TempTest tempTest = new TempTest();
        Server server = new Server(tempTest);
        MainSocket ms = new MainSocket(13814, server);
        new Thread(ms).start();
    }

    @Override
    public void handleFatalError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void handleError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void handleWarning(String warning) {
        System.out.println(warning);
    }

    @Override
    public void handleInfo(String info) {
        System.out.println("[CLIENT] " + info);
    }
}
