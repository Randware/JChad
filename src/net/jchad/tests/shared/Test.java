package net.jchad.tests.shared;

import net.jchad.server.model.users.User;

public class Test {
    public static void main(String[] args) {
       // User user = new User("Test user", null, );
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
    }
}
