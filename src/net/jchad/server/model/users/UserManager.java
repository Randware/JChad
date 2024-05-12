package net.jchad.server.model.users;

import net.jchad.server.model.server.Server;
import net.jchad.server.model.server.ServerThread;

import java.security.spec.KeySpec;


public abstract class UserManager {

    private final String anonymousUsernamePrefix = "User";
    private final String anonymousUsernameIdentifier = "0123456789";
    private final int anonymousUsernameIdentifierLength = 6; //TODO number <=0 should assign a dynamic length
    private UserManager(ServerThread serverThread, String username) {

    }

    //TODO implement it class
}
