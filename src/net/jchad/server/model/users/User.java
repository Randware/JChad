package net.jchad.server.model.users;

import net.jchad.server.model.server.ServerThread;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class User {

    private static final ConcurrentHashMap<String, ServerThread> users = new ConcurrentHashMap<>();
    private final ServerThread connection;
    private final String username;


    public User(String username, ServerThread connection) {
        if (username == null) {
            throw new NullPointerException("username can not be null");
        }

        if (connection == null) {
            throw new NullPointerException("the connection is not allowed to be null");
        }

        if (users.contains(username)) {
            throw new UsernameTakenException("The username: " + username + " is already taken.");
        }

        if (users.containsValue(connection)) {
            throw new ConnectionExistsException("The connection (" + connection.getInetAddress().toString() + ") has already been associated with a username");
        }

        this.connection = connection;
        this.username = username;

        users.put(username, connection);

    }

    /**
     *
     * @param connection The connection that should get removed
     * @return true if an element was removed as a result of this call
     */
    public static boolean removeUser(ServerThread connection) {
     synchronized (users) {
        return users.values().remove(connection);
        }
    }

    /**
     *
     * @param username
     * @return true if an element was removed as a result of this call
     */
    public static boolean removeUser(String username) {
        ServerThread removed;
        synchronized (users) {
             removed = users.remove(username);
        }
        if (removed == null) {return false;}
        else {return true;}
    }


}
