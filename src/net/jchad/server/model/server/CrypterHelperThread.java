package net.jchad.server.model.server;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.InvalidPacket;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.PublicRSAkeyPacket;

import java.io.IOException;

public class CrypterHelperThread {
    private final ServerThread serverThread;
    private final int retries;
    private final CrypterManager crypterManager = new CrypterManager();

    protected CrypterHelperThread(ServerThread serverThread) {
        this.serverThread = serverThread;
        retries = serverThread.getRetriesOnInvalidPackets();
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
                        PublicRSAkeyPacket publicRSAkeyPacket = serverThread.getMainSocket().getGson().fromJson(serverThread.getJsonReader(), PublicRSAkeyPacket.class);
                        if (!publicRSAkeyPacket.isValid()) {
                            throw new InvalidPacketException("The received PublicRSAkeyPacket is not valid.");
                        }

                    } else {
                        skip = true;
                        throw new MalformedJsonException("Invalid JSON while trying to exchange RSA public keys");
                    }

                }
            } catch (MalformedJsonException | JsonSyntaxException | InvalidPacketException e) {
                if (fails >= retries) {
                    serverThread.getMessageHandler().handleDebug("%s sent an invalid packet. The connection get terminated now after %d failed attempts from %d maximum tries".formatted(serverThread.getRemoteAddress(), fails, retries));
                    serverThread.close("%s sent to many invalid packets".formatted(serverThread.getRemoteAddress()));
                    break;
                } else {
                    serverThread.getMessageHandler().handleDebug("%s sent an invalid packet! The connection gets terminated if the server receives %d more invalid packets".formatted(serverThread.getRemoteAddress(), retries - fails));
                    serverThread.getPrintWriter().println(new InvalidPacket(PacketType.RSA_PUBLIC_KEY, "The provided RSA packet was not valid"));
                }
            }
            catch (IOException ioe) {
                serverThread.getMessageHandler().handleDebug(new IOException("An IO exception occurred! ", ioe));
                serverThread.close("An IO exception occurred while trying to exchange the RSA keys");
                break;
            }
        }
    }
}
