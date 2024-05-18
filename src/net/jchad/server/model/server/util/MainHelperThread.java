package net.jchad.server.model.server.util;

import com.google.gson.JsonSyntaxException;
import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.InvalidPacket;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationRequestPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationResponsePacket;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.PacketType;
import net.jchad.shared.networking.packets.messages.LoadChatRequestPacket;
import net.jchad.shared.networking.packets.messages.LoadChatResponsePacket;

/**
 * This is the main helper thread that gets used when everything is initialized
 */
public class MainHelperThread extends HelperThread {

    private final int fails = 1;
    public MainHelperThread(ServerThread serverThread) {
        super(serverThread);
    }

    /**
     * This methode gets called if the set-up steps are finished in the {@link ServerThread}
     * The methode can receive 4 different Packets:
     * <o>
     *     <li>A {@link ClientMessagePacket MessagePacket}</li>
     *     <li>A {@link LoadChatRequestPacket JoinChatPacket}</li>
     *     <li>A {@link ConnectionClosedPacket}</li>
     *     <li>A {@link  ServerInformationRequestPacket ServerInformationPacket}</li>
     * </o>
     */
    public void  start() {
        int retries = getRetries();
        for (int failedAttempts = 0; retries >= failedAttempts; failedAttempts++) {
            try {
                Thread.currentThread().sleep(getSleepInterval());
                String element = getServerThread().getScanner().nextLine();
                //This checks if the client sends one of these 4 packet types

                //First check: Checks if the client sent a messagePacket
                ClientMessagePacket clientMessage = getServerThread().getGson().fromJson(element, ClientMessagePacket.class);
                if (clientMessage != null && clientMessage.isValid()) {
                    getServerThread().getUser().sendMessage(clientMessage);
                    failedAttempts--;
                    continue;
                }

                //Second check: Checks if the client sent a JoinChatPacket
                LoadChatRequestPacket joinChat = getServerThread().getGson().fromJson(element, LoadChatRequestPacket.class);
                if (joinChat != null &&joinChat.isValid()) {
                    Chat chat =  getServerThread().getServer().getChatManager().getChat(joinChat.getChat_name());
                    if (chat == null) {
                        throw new InvalidPacketException("The given chat does not exist");
                    }
                    writeJSON(new LoadChatResponsePacket(chat.getName(),chat.getMessages()).toJSON());
                    failedAttempts--;
                    continue;
                }

                //Third check: Checks if the client closed the connection
                ConnectionClosedPacket connectionClosed = getServerThread().getGson().fromJson(element, ConnectionClosedPacket.class);
                if (connectionClosed != null &&connectionClosed.isValid()) {
                    getServerThread().close("The client disconnected");
                    break; //<--- This point of code should not be reached
                }

                //Fourth check: Checks if the client asks for the server information.
                ServerInformationRequestPacket informationPacket = getServerThread().getGson().fromJson(element, ServerInformationRequestPacket.class);
                if (informationPacket != null && informationPacket.isValid()) {
                    writeJSON(ServerInformationResponsePacket.getCurrentServerInfo(getServerThread().getServer()).toJSON());
                    failedAttempts--;
                    continue;
                }

                    //If none of the above matched, the given packet is invalid.
                    throw new InvalidPacketException("The packet that the client sent was not recognized");
            } catch (JsonSyntaxException | InvalidPacketException e) {
                if (failedAttempts >= retries) {
                    getServerThread().getMessageHandler().handleDebug("%s sent to many invalid packets. The connection get terminated now!".formatted(getServerThread().getRemoteAddress()));
                    getServerThread().close("The client exceeded the invalid packets limit. " +
                            e.getMessage());
                    break;
                } else {
                    getServerThread().getMessageHandler().handleDebug("%s sent to many invalid packets. The connection gets terminated if the server receives %d more invalid packet(s)".formatted(getServerThread().getRemoteAddress(), retries - failedAttempts));
                    writeJSON(new InvalidPacket(PacketType.CLIENT_MESSAGE, "The provided packet was not valid. " +
                            e.getMessage()).toJSON());
                }
            } catch (InterruptedException e) {
                getServerThread().getMessageHandler().handleError(new Exception("The client thread connected with %s got interrupted unsuspectingly, during the main execution".formatted(getServerThread().getRemoteAddress()), e));
                getServerThread().close("Thread got interrupted unsuspectingly");
            }
        }
    }


}
