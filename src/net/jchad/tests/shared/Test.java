package net.jchad.tests.shared;

import net.jchad.shared.cryptography.CrypterManager;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.username.UsernameClientPacket;

public class Test {
    public static void main(String[] args) {
       // User user = new User("Test user", null, );
        System.out.println(CrypterManager.hash("Test"));
        boolean test0 = "".matches("^[A-Za-z]+(?:_[A-Za-z]+)?$");
        boolean test1 = "dari_os".matches("^[A-Za-z]+(?:_[A-Za-z]+)?$");
        boolean test2 = "GHaxZ".matches("^[A-Za-z]+(?:_[A-Za-z]+)?$");
        boolean test3 = "dari0s".matches("^[A-Za-z]+(?:_[A-Za-z]+)?$");
        boolean test4 = "_dari_".matches("^[A-Za-z]+(?:_[A-Za-z]+)?$");
        boolean test5 = "dari_os_os".matches("^[A-Za-z]+(?:_[A-Za-z]+)?$");

        System.out.println(test0);
        System.out.println(test1);
        System.out.println(test2);
        System.out.println(test3);
        System.out.println(test4);
        System.out.println(test5);


        System.out.println(new UsernameClientPacket("Dari_OS").toJSON());
        System.out.println(new ClientMessagePacket("Hello world", false, "test").toJSON());
    }
}
