package net.jchad.server.model.config.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import net.jchad.server.model.config.store.internalSettings.InternalSettings;
import net.jchad.server.model.config.store.serverSettings.ServerSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public enum ConfigFiles {
    SERVER_SETTINGS_CONFIG("server-settings.yml", new ServerSettings()),
    INTERNAL_SETTINGS_CONFIG("internal-settings.yml", new InternalSettings()),
    WHITELISTED_IPS_CONFIG("whitelisted-ips.yml", new ArrayList<>()),
    BLACKLISTED_IPS_CONFIG("blacklisted-ips.yml", new ArrayList<>());

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
     * The class this config file will be deserialized to
     */
    private Object storageObject;

    private ObjectMapper mapper;

    /**
     * This creates the ConfigFiles enums by combining the <code>storageDirectory</code> string with their file name.
     *
     * @param fileName the file name this specific config will be stored under
     */
    ConfigFiles(String fileName, Object storageObject) {
        this.storagePath = storageDirectory + fileName;
        this.fileName = fileName;
        this.storageObject = storageObject;

        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    }

    /**
     * @return the {@link Path} where this config is stored
     */
    public Path getStoragePath() {
        return Path.of(this.storagePath);
    }

    /**
     * @return the {@link String} file name this config is stored under
     */
    public String getFileName() {
        return fileName;
    }

    /**
     *
     * @return the object this config file will be deserialized to
     */
    public Object getStorageObject() {
        return storageObject;
    }

    /**
     *
     * @return the {@link Path} where config files will be stored
     */
    public static Path getStorageDirectory() {
        return Path.of(storageDirectory);
    }

    public Object load() throws Exception {
        return mapper.readValue(getStoragePath().toFile(), storageObject.getClass());
    }

    public void save(Object saveObject) throws IOException {
        mapper.writeValue(getStoragePath().toFile(), saveObject);
    }

    public void create() throws IOException {
        Files.createFile(getStoragePath());
    }

    public boolean exists() {
        return Files.exists(getStoragePath());
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
