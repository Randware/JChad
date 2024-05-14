package net.jchad.server.model.users;

import net.jchad.server.model.server.ServerThread;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class User {

    private static final ConcurrentHashMap<ServerThread, User> users = new ConcurrentHashMap<>();
    private final ServerThread connection;
    private final String username;
    private final Set<String> joinedChats = ConcurrentHashMap.newKeySet();

    public User(String username, ServerThread connection) {
        this(username, connection, new HashSet<>());
    }

    public User(String username, ServerThread connection, Set<String> joinedChats) {
        if (username == null) {
            throw new NullPointerException("username can not be null");
        }

        if (connection == null) {
            throw new NullPointerException("the connection is not allowed to be null");
        }

        if (joinedChats == null) {
            throw new NullPointerException("the joinedChats is not allowed to be null");
        }

        if (users.contains(username)) {
            throw new UsernameTakenException("The username: " + username + " is already taken.");
        }

        if (users.containsValue(connection)) {
            throw new ConnectionExistsException("The connection (" + connection.getInetAddress().toString() + ") has already been associated with a username");
        }

        this.connection = connection;
        this.username = username;
        this.joinedChats.addAll(joinedChats);
        users.put(connection, this);

    }

    public ServerThread getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public void addJoinedChats(String... chats) {
        joinedChats.addAll(Arrays.asList(chats));
    }

    public void removeJoinedChats(String... chats) {
        joinedChats.removeAll(Arrays.asList(chats));
    }

    public Set<String> getJoinedChats() {
        return joinedChats;
    }



    /**
     *
     * @param connection The connection that should get removed
     * @return true if an element was removed as a result of this call
     */
    public static boolean removeUser(ServerThread connection) {
        User removed;
        removed = users.remove(connection);
        if (removed == null) {return false;}
        else {return true;}
    }

    /**
     *
     * @param username The username that should get removed
     * @return true if an element was removed as a result of this call
     */
    public static boolean removeUser(String username) {
        boolean wasRemoved = false;
        Iterator<User> iterator = users.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getUsername().equals(username)) {
                iterator.remove();
                wasRemoved = true;
                break;
            }
        }

        return wasRemoved;

    }
    /**
     *
     * @param user The user that should get removed
     * @return true if an element was removed as a result of this call
     */
    public static boolean removeUser(User user) {
            return users.values().remove(user);
    }

}
