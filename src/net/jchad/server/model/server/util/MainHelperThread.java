package net.jchad.server.model.server.util;

import com.google.gson.JsonSyntaxException;
import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.server.ConnectionClosedException;
import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.InvalidPacket;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;
import net.jchad.shared.networking.packets.defaults.ConnectionEstablishedPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationRequestPacket;
import net.jchad.shared.networking.packets.defaults.ServerInformationResponsePacket;
import net.jchad.shared.networking.packets.messages.*;
import net.jchad.shared.networking.packets.PacketType;

import java.util.List;

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
     *     <li>A {@link JoinChatRequestPacket JoinChatPacket}</li>
     *     <li>A {@link ConnectionClosedPacket}</li>
     *     <li>A {@link  ServerInformationRequestPacket ServerInformationPacket}</li>
     * </o>
     */
    public void  start() {
        getServerThread().getMessageHandler().handleDebug("%s finished the initialization steps. The MainHelperThread gets started ".formatted(getServerThread().getRemoteAddress()));
        writePacket(new ConnectionEstablishedPacket());
        writePacket(ServerInformationResponsePacket.getCurrentServerInfo(getServerThread().getServer()));
        int retries = getRetries();
        for (int failedAttempts = 0; retries >= failedAttempts; failedAttempts++) {
            try {
                Thread.sleep(getSleepInterval());
                String element = getServerThread().next();

                if (element == null) throw new ConnectionClosedException();
                //This checks if the client sends one of these 4 packet types

                //First check: Checks if the client sent a messagePacket
                ClientMessagePacket clientMessage = getServerThread().getGson().fromJson(element, ClientMessagePacket.class);
                if (clientMessage != null && clientMessage.isValid()) {
                    if (getServerThread().getServer().getChatManager().chatExists(clientMessage.getChat())) {
                            getServerThread().getUser().sendMessage(clientMessage);
                        writePacket(new MessageStatusSuccessPacket());
                        failedAttempts--;
                    } else {
                        writePacket(new MessageStatusFailedPacket("The provided chat does not exist"));
                    }
                    continue;
                }

                //Second check: Checks if the client sent a JoinChatPacket
                JoinChatRequestPacket joinChat = getServerThread().getGson().fromJson(element, JoinChatRequestPacket.class);
                if (joinChat != null &&joinChat.isValid()) {
                    Chat chat =  getServerThread().getServer().getChatManager().getChat(joinChat.getChat_name());
                    if (chat == null) {
                        throw new InvalidPacketException("The given chat does not exist");
                    }
                    writePacket(new JoinChatResponsePacket(chat.getName(),chat.getServerMessages()));
                    getServerThread().getUser().addJoinedChats(joinChat.getChat_name());
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
                    writePacket(ServerInformationResponsePacket.getCurrentServerInfo(getServerThread().getServer()));
                    failedAttempts--;
                    continue;
                }

                    //If none of the above matched, the given packet is invalid.
                    throw new InvalidPacketException("The packet that the client sent was not recognized");
            }  catch (JsonSyntaxException | InvalidPacketException e) {
                if (failedAttempts >= retries) {
                    getServerThread().getMessageHandler().handleDebug("%s sent to many invalid packets. The connection get terminated now!".formatted(getServerThread().getRemoteAddress()));
                    getServerThread().close("The client exceeded the invalid packets limit. " +
                            e.getMessage());
                    break;
                } else {
                    getServerThread().getMessageHandler().handleDebug("%s sent to many invalid packets. The connection gets terminated if the server receives %d more invalid packet(s)".formatted(getServerThread().getRemoteAddress(), retries - failedAttempts));
                    writePacket(new InvalidPacket(List.of(PacketType.CLIENT_MESSAGE, PacketType.JOIN_CHAT_REQUEST, PacketType.CONNECTION_CLOSED, PacketType.SERVER_INFORMATION_REQUEST)
                            , "The provided packet was not valid. " +
                            e.getMessage()));
                }
            } catch (InterruptedException e) {
               //Thread got probably closed
            }
        }
    }


}
