package net.jchad.tests.shared;

import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.CrypterUtil;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.encryption.RSAkeyPacket;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.messages.JoinChatRequestPacket;
import net.jchad.shared.networking.packets.password.PasswordResponsePacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ImpossibleConversionException, InvalidAlgorithmParameterException {


        System.out.println(new ClientMessagePacket("Hello world", "test").toJSON());
        System.out.println(new JoinChatRequestPacket("test").toJSON());
        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        /*
        {"packet_type":"CLIENT_MESSAGE","message":"Hello world","chat":"test"}
        {"packet_type":"JOIN_CHAT_REQUEST","chat_name":"test"}
        {"packet_type":"USERNAME_CLIENT","username":"Dari_OS"}
         */
        System.out.println(Packet.fromJSON("{\"packet_type\":\"CLIENT_MESSAGE\",\"message\":\"Hello world\",\"chat\":\"test\"}").getClass().getSimpleName());
    }
}
