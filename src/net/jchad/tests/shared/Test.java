package net.jchad.tests.shared;

import net.jchad.client.model.client.connection.ClosedConnectionException;
import net.jchad.server.model.server.ConnectionClosedException;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.messages.JoinChatRequestPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException, InvalidAlgorithmParameterException, ClosedConnectionException, IOException, InvalidPacketException {


        System.out.println(new PasswordResponsePacket(CrypterManager.hash("test")).toJSON());
        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        System.out.println(new ClientMessagePacket("Hello lovely world", "test").toJSON());
        System.out.println(new JoinChatRequestPacket("test").toJSON());
        readPacket(new PasswordResponsePacket(CrypterManager.hash("test")).toJSON());
        System.out.println(new ConnectionClosedPacket("").toJSON());
        System.out.println(new ConnectionClosedPacket("").isValid());
        readPacket("{\"packet_type\":\"CONNECTION_CLOSE\",\"message\":\"\"}");


    }


    public static  <T extends Packet> T readPacket(String testString) throws InvalidPacketException, IOException, ClosedConnectionException {
        String read = testString;
        Packet packet = Packet.fromJSON(read);
        if (packet != null && packet.isValid()) {
            if (packet.getClass().equals(ConnectionClosedPacket.class)){
                ConnectionClosedPacket closedPacket = (ConnectionClosedPacket) packet;
                throw new ConnectionClosedException("Connection was closed by the server. Reason: " + closedPacket.getMessage());
            } else {
                System.out.println(packet.getClass().getSimpleName());
                return (T) packet;
            }
        } else {
            throw new InvalidPacketException("The packet that was read is invalid");
        }
    }
}
