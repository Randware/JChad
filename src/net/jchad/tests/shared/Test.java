package net.jchad.tests.shared;

import net.jchad.client.model.client.connection.ClosedConnectionException;
import net.jchad.server.model.server.ConnectionClosedException;
import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.cryptography.ImpossibleConversionException;
import net.jchad.shared.networking.packets.InvalidPacketException;
import net.jchad.shared.networking.packets.Packet;
import net.jchad.shared.networking.packets.defaults.ConnectionClosedPacket;
import net.jchad.shared.networking.packets.encryption.RSAkeyPacket;
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


        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        System.out.println(new ClientMessagePacket("Hello world", "test").toJSON());
        System.out.println(new JoinChatRequestPacket("test").toJSON());

        /*System.out.println(new PasswordResponsePacket(CrypterManager.hash("test")).toJSON());
        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        System.out.println(new ClientMessagePacket("Hello lovely world", "test").toJSON());
        System.out.println(new JoinChatRequestPacket("test").toJSON());
        CrypterManager crypterManager = new CrypterManager();
        crypterManager.setKeyPair(4096);
        //Public: MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAh9QmMG18qPWe8WERUMZX9fN5/diwAOFBFksSnK8kGtcflaL+F5aqFp25Pz+EZEQnqiSFfWJ1RdzcsEL/SyWsSpV2usdiddD+BRBKW+i8APSFqjc5vsCF7F0RMSd0F37MVG2WjsUzgig+4NBS0NHC4I5mKiV8O+ZV8qq+UOe/EtlJPDWAHDWSO3TIkWKJveGqt9uYU2xItjSCGvmNyo3NeE+Hmz2/VvMIDxEc0XxHLAEa8d4ZbAjTZwa03VQGy5EODdqLIgC3ETyCQOk9O/4EjUEHifY2zIfPudVjZkRW6REfKysTvckAvYai+Rq5xO2EWcjbiB755kKUyP74a0S0XILE4vRj1j5upI0cREghu0dPnSO9fk3igEnV5Dceyyf5wrLVprjQFKrrVHctf7zWLcRdxXJqz8OMvladLMxsPADDaw8ZXbHvlutOEWcqmbv/e2VC3dvr1KZjjah/JvSPl9aMIM6+DznQD6/fLExScGuLaWxF/4WVgko6hZPr6lVb0qjdgOm1hX7pOylJdVHoEFXt+9fLRlpT9mfsIJWdovjX42OlT6Xt6jhGiJcjUCwFFNzNtr7dv/mwLpogUReULpBMGKyzFA6dOYoCZCUXif9z0oYCaY+77SUtimw6SkGSmIVTZQnrJnBUpZpOEvl1/iQBS5PaaA7X2qOXagrd3TsCAwEAAQ==
        //Private: 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
        System.out.println(new RSAkeyPacket("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAh9QmMG18qPWe8WERUMZX9fN5/diwAOFBFksSnK8kGtcflaL+F5aqFp25Pz+EZEQnqiSFfWJ1RdzcsEL/SyWsSpV2usdiddD+BRBKW+i8APSFqjc5vsCF7F0RMSd0F37MVG2WjsUzgig+4NBS0NHC4I5mKiV8O+ZV8qq+UOe/EtlJPDWAHDWSO3TIkWKJveGqt9uYU2xItjSCGvmNyo3NeE+Hmz2/VvMIDxEc0XxHLAEa8d4ZbAjTZwa03VQGy5EODdqLIgC3ETyCQOk9O/4EjUEHifY2zIfPudVjZkRW6REfKysTvckAvYai+Rq5xO2EWcjbiB755kKUyP74a0S0XILE4vRj1j5upI0cREghu0dPnSO9fk3igEnV5Dceyyf5wrLVprjQFKrrVHctf7zWLcRdxXJqz8OMvladLMxsPADDaw8ZXbHvlutOEWcqmbv/e2VC3dvr1KZjjah/JvSPl9aMIM6+DznQD6/fLExScGuLaWxF/4WVgko6hZPr6lVb0qjdgOm1hX7pOylJdVHoEFXt+9fLRlpT9mfsIJWdovjX42OlT6Xt6jhGiJcjUCwFFNzNtr7dv/mwLpogUReULpBMGKyzFA6dOYoCZCUXif9z0oYCaY+77SUtimw6SkGSmIVTZQnrJnBUpZpOEvl1/iQBS5PaaA7X2qOXagrd3TsCAwEAAQ==").toJSON());

        System.out.println(new ConnectionClosedPacket("").toJSON());*/




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
