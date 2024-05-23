package net.jchad.client.model.client.connection;

public record ServerConfig(double server_version, boolean encrypt_communications, boolean encrypt_messages,
                           String[] available_chats, boolean requires_password, boolean whitelist,
                           boolean strictly_anonymous, String username_validation_regex, String username_validation_description) {
}
