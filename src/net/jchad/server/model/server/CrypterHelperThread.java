package net.jchad.server.model.server;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.networking.packets.PublicRSAkeyPacket;

import java.io.IOException;

public class CrypterHelperThread {
    private final ServerThread serverThread;
    private final String base64AESmessageKey;
    private final int retries;
    private final CrypterManager crypterManager = new CrypterManager();

    protected CrypterHelperThread(ServerThread serverThread, String base64AESmessageKey, int retries) {
        this.serverThread = serverThread;
        this.base64AESmessageKey = base64AESmessageKey;
        this.retries = retries;

    }

    public void exchangeRSAkeys() {
        crypterManager.initKeyPair();
        serverThread.getPrintWriter().println(new PublicRSAkeyPacket(crypterManager.getPublicKey()));
        int retries = this.retries;
        boolean skip = false;
        for (int fails = 1; fails <= retries; fails++) {
            try {
                while (serverThread.getJsonReader().hasNext()) {
                    if (skip) {
                        serverThread.getJsonReader().skipValue();
                        skip = false;
                    }
                    JsonToken jsToken = serverThread.getJsonReader().peek();
                    if (jsToken.equals(JsonToken.BEGIN_OBJECT)) {

                    } else {
                        skip = true;
                        throw new MalformedJsonException("Invalid JSON while trying to exchange RSA public keys");
                    }

                }
            } catch (MalformedJsonException | JsonSyntaxException e) {
                
            }
            catch (IOException ioe) {
                serverThread.getMessageHandler().handleError(new IOException("An IO exception occurred! ", ioe));
                serverThread.close("An IO exception occurred while trying to exchange the RSA keys");
            }
        }
    }
}
