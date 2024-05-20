package net.jchad.server.model.users;

import net.jchad.server.model.chats.Chat;
import net.jchad.server.model.chats.ChatMessage;
import net.jchad.server.model.server.ServerThread;
import net.jchad.shared.networking.packets.messages.ClientMessagePacket;
import net.jchad.shared.networking.packets.messages.ServerMessagePacket;
import net.jchad.shared.networking.packets.messages.MessagePacket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class User {

    private static final ConcurrentHashMap<ServerThread, User> users = new ConcurrentHashMap<>();
    private final ServerThread connection;
    private final String username;
    private final Set<String> joinedChats = ConcurrentHashMap.newKeySet();
    private boolean readyToReceiveMessages;

    /**
     * Creates a new user and adds it and the given {@link ServerThread} into a {@link ConcurrentHashMap}.
     * Users can send messages to other users
     *
     * @param username The username of the user.
     * @param connection The {@link ServerThread} that gets associated with the user
     *
     *
     * @throws UsernameInvalidException If the username did not match with the configured regex
     * @throws UsernameTakenException If the username was already taken by another connection
     * @throws NullPointerException If one of the Parameters is null
     * @throws ConnectionExistsException If the connection is already associated with a user
     */
    public User(String username, ServerThread connection) {
            this(username, connection, new HashSet<>());
        }


    /**
     * Creates a new user and adds it and the given {@link ServerThread} into a {@link ConcurrentHashMap}.
     * Users can send messages to other users
     *
     * @param username The username of the user.
     * @param connection The {@link ServerThread} that gets associated with the user
     * @param joinedChats All chats that the user is in
     *
     *
     * @throws UsernameInvalidException If the username did not match with the configured regex
     * @throws UsernameTakenException If the username was already taken by another connection
     * @throws NullPointerException If one of the Parameters is null
     * @throws ConnectionExistsException If the connection is already associated with a user
     */
    public User(String username, ServerThread connection, Set<String> joinedChats) {
        this(username, connection, joinedChats, false);
    }

    /**
     * Creates a new user and adds it and the given {@link ServerThread} into a {@link ConcurrentHashMap}.
     * Users can send messages to other users
     *
     * @param username The username of the user.
     * @param connection The {@link ServerThread} that gets associated with the user
     * @param readyToReceiveMessages If the user is ready to receive chats, or if some further initialization steps have to be done.
     *                               Change this state with {@link User#setReadyToReceiveMessages(boolean)}.
     *
     *
     * @throws UsernameInvalidException If the username did not match with the configured regex
     * @throws UsernameTakenException If the username was already taken by another connection
     * @throws NullPointerException If one of the Parameters is null
     * @throws ConnectionExistsException If the connection is already associated with a user
     */
    public User(String username, ServerThread connection,Set<String> joinedChats, boolean readyToReceiveMessages) {
        if (username == null) {
            throw new NullPointerException("username can not be null");
        }

        if (connection == null) {
            throw new NullPointerException("the connection is not allowed to be null");
        }

        if (joinedChats == null) {
            throw new NullPointerException("the joinedChats is not allowed to be null");
        }

        if (!username.matches(connection.getServer().getConfig().getInternalSettings().getUsernameRegex())) {
            throw new UsernameInvalidException(connection.getServer().getConfig().getInternalSettings().getUsernameRegexDescription() ,username);
        }
        boolean usernameExists = false;
        Collection<User> checkWithUsers = users.values();
        for (User currentUser : checkWithUsers) {
            if (currentUser.getUsername().equalsIgnoreCase(username)) {
                usernameExists = true;
                break;
            }
        }

        if (usernameExists) {
            throw new UsernameTakenException("The username: " + username + " is already taken.");
        }

        if (users.containsValue(connection)) {
            throw new ConnectionExistsException("The connection (" + connection.getInetAddress().toString() + ") has already been associated with a username");
        }

        this.connection = connection;
        this.username = username;
        this.joinedChats.addAll(joinedChats);
        this.readyToReceiveMessages = readyToReceiveMessages;
        users.put(connection, this);

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

    public ServerThread getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public boolean isReadyToReceiveMessages() {
        return readyToReceiveMessages;
    }

    public void setReadyToReceiveMessages(boolean readyToReceiveMessages) {
        this.readyToReceiveMessages = readyToReceiveMessages;
    }



    /**
     * This methode wraps the {@link ClientMessagePacket} to a {@link ServerMessagePacket} by inserting the current time and the username of this instance.
     * The given message gets sent to all valid users afterwards.
     * A valid user is someone that:
     * <ul>
     *     <li> Is ready to receive messages ({@link User#readyToReceiveMessages} has to be {@code true}) </li>
     *
     *     <li> Does not equal the current user. Object.equals(this, user) has to be false. </li>
     *
     *     <li> Has already joint the specified chat in the {@link MessagePacket messagePacket} </li>
     * </ul>
     * @param messagePacket the packet that gets send to the valid users
     * @return to how many users the {@link MessagePacket messagePacket} was sent
     */
    public int sendMessage(ClientMessagePacket messagePacket) {
        return sendMessage(new ServerMessagePacket(
                messagePacket.getMessage(),
                messagePacket.getChat(),
                getUsername(),
                System.currentTimeMillis()
        ));
    }

    /**
     * This methode sends the given message to all valid users.
     * A valid user is someone that:
     * <ul>
     *     <li> Is ready to receive messages ({@link User#readyToReceiveMessages} has to be {@code true}) </li>
     *
     *     <li> Does not equal the current user. Object.equals(this, user) has to be false. </li>
     *
     *     <li> Has already joint the specified chat in the {@link MessagePacket messagePacket} </li>
     * </ul>
     * @param messagePacket the packet that gets sent to the valid users. If the username is null, it gets set to the username of this instance.
     * @return to how many users the {@link MessagePacket messagePacket} was sent
     */
    public int sendMessage(ServerMessagePacket messagePacket) {
        int messagesSent = 0;
        if (messagePacket == null) {return 0;}
        if (messagePacket.getUsername() == null) messagePacket.setUsername(this.getUsername());
        if (!messagePacket.isValid()) {return 0;}
        Chat chat = connection.getServer().getChatManager().getChat(messagePacket.getChat());
        ChatMessage chatMessage = ChatMessage.fromMessagePacket(messagePacket, connection.getRemoteAddress());
        if (chat == null) {
            connection.getMessageHandler().handleDebug("%s tried to send a message to a chat that does not exist".formatted(getConnection().getRemoteAddress()));
            return 0;
        }
            Collection<User> userValues = users.values();
            for (User user : userValues) {
                if (!this.equals(user) && user.isReadyToReceiveMessages() && user.getJoinedChats().contains(messagePacket.getChat())) {
                    try {
                        chat.addMessage(chatMessage);
                    } catch (IOException e) {
                        connection.getMessageHandler().handleError(new IOException("An IOException occurred while trying to add the message (from %s) to the chat (%s)"
                                .formatted(connection.getRemoteAddress(), chat.getName()), e));
                    }
                    user.getConnection().write(messagePacket.toJSON());
                    messagesSent++;
                }

            }

        return messagesSent;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return readyToReceiveMessages == user.readyToReceiveMessages && Objects.equals(connection, user.connection) && Objects.equals(username, user.username) && Objects.equals(joinedChats, user.joinedChats);
    }



}
