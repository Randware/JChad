package net.jchad.server.model.config;

import java.nio.file.Path;

public enum ConfigFiles {
    SERVER_CONFIG("server-config.yml"),
    WHITELISTED_IPS_CONFIG("whitelisted-ips.yml"),
    BLACKLISTED_IPS_CONFIG("blacklisted-ips.yml");

    /**
     * This is where the config files will be stored.
     */
    private static final String storageDirectory = "./configs/";

    /**
     * The file path string, where this specific config is stored.
     */
    private String storagePath;

    /**
     * The file name this specific config will be stored under.
     */
    private String fileName;

    /**
     * This creates the ConfigFiles enums by combining the <code>storageDirectory</code> string with their file name.
     *
     * @param fileName the file name this specific config will be stored under
     */
    ConfigFiles(String fileName) {
        this.storagePath = storageDirectory + fileName;
        this.fileName = fileName;
    }

    /**
     *
     * @return the {@link Path} where this config is stored
     */
    public Path getStoragePath() {
        return Path.of(this.storagePath);
    }

    /**
     *
     * @return the {@link String} file name this config is stored under
     */
    public String getFileName() {
        return fileName;
    }

    /**
     *
     * @return the {@link Path} where config files will be stored
     */
    public static Path getStorageDirectory() {
        return Path.of(storageDirectory);
    }

    /**
     * This method checks if a config file name is a config used by the program.
     *
     * @param configName the config name that should be checked
     * @return <code>true</code> if this is a config, <code>false</code> if not
     */
    public static boolean isConfig(String configName) {
        for(ConfigFiles config : ConfigFiles.values()) {
            if(config.getFileName().equals(configName)) {
                return true;
            }
        }

        return false;
    }
}
