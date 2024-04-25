package net.jchad.server.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.networking.ip.IPAddress;
import net.jchad.server.model.networking.ip.InvalidIPAddressException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;

/*

    TODO: Make ConfigWatcher restart itself, when an error occurs
        - As soon as ConfigWatcher fails, try restarting (maybe twice),
        if it still fails, stop attempting to restart and disable the ConfigWatcher feature
    TODO: Stop reloading server config, when whitelist is disabled and gets modified
        - Pass the current config to the isConfig() method in the ConfigFiles class and only check enabled files
    TODO: Investigate missing values in config (server uses default value for missing values,
        I don't know why, I didn't implement this knowingly)
        - Log missing value warning
    TODO: Implement configuration modification during runtime from within the program
        - Create set methods that modify config values directly, then notify server of changed config
        - Need to write the changes to file without notifying the ConfigWatcher (maybe bring back the runUnsupervised() concept?)
        - create method that pauses ConfigWatcher, then saves config, then continues ConfigWatcher
    DONE: Fix IP string parsing ("192.168.0.1" for example is not valid) -> Wrote own implementation
        - This happens because the InetAddress class does a ip lookup every time and if it doesn't find the ip,
        it is deemed invalid. In addition to that, the validation process is very slow because of that.
        - Maybe use Guava library for IP address parsing (really don't want to write my own IP parser)
        - Maybe write own classes using regex
    DONE: Store config files in ConfigFiles enum, so querying data about them becomes easier
    CANCELED: Maybe don't dynamically load whitelisted and blacklisted files
    DONE: Fix bug, where the program "fails" updating the config when creating it
        - Only happens if config directory not created yet, only happens in jar outside of IDE
            - Creating config directory before running ConfigWatcher on it seems to fix it
    DONE: Rewrite structure, so error handling and logging becomes easier and less hacky
    DONE: Handle MismatchedInputException
        - This exception occurs when the ObjectMapper tries to parse an empty file
    DONE: Implement validation for config files
        - Catch Jackson format exception
    DONE: Get rid of runUnsupervised() method and concept, since this promotes bad code structure
    DONE: Fix live reloading multiple times
        - check if modified file is actual config file (or just temp file, user created file, etc.)
        - prevent same file edit event being recognized twice
    DONE: Inform ConfigManager which WatchEvent has occurred
        - can check if the updated file is an actual config file (mentioned in previous entry)
        - can also log which file was changed
    DONE: Fix live reloading multiple times -> pause ConfigWatcher when saving configs from program
 */

public class ConfigManager {
    /**
     * Used for YAML file reading and writing using the {@link com.fasterxml.jackson} library
     */
    private final ObjectMapper mapper;

    /**
     * Stores the instance of the {@link net.jchad.server.model.config.ConfigWatcher} instance,
     * used for watching for file changes in config files.
     */
    private ConfigWatcher configWatcher;

    /**
     *
     */
    private int restartAttempts = 0;

    /**
     *
     */
    private final int MAX_RESTART_ATTEMPTS = 3;

    /**
     *
     */
    private long lastConfigWatcherStart;

    /**
     * Stores the instance of a {@link ConfigObserver} implementing class,
     * that can be passed in the constructor, which gets notified when config file changes are detected.
     */
    private ConfigObserver configObserver;

    /**
     * Stores the instance of a {@link MessageHandler} implementing class,
     * which is responsible for displaying and handling errors, warnings and logs.
     */
    private MessageHandler messageHandler;

    /**
     * Stores the newest loaded config file.
     */
    private Config config;

    /**
     * @param messageHandler {@link MessageHandler} implementing class instance responsible for handling errors,
     *                       warnings and logs
     */
    public ConfigManager(MessageHandler messageHandler) {
        this(messageHandler, null);
    }

    /**
     * @param messageHandler {@link MessageHandler} implementing class instance responsible for handling errors,
     *                       warnings and logs
     * @param configObserver {@link ConfigObserver} implementing class instance,
     *                       which gets notified when config file changes are detected.
     */
    public ConfigManager(MessageHandler messageHandler, ConfigObserver configObserver) {
        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

        this.messageHandler = messageHandler;
        this.configObserver = configObserver;

        loadServerConfig();

        initializeConfigWatcher();
    }

    /**
     * Returns the currently loaded config.
     *
     * @return {@link Config} that got loaded
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Handles the file loading process and updates the config. Also logs accordingly.
     *
     * @return <code>true</code> config was updated successfully, <code>false</code> if not
     */
    private boolean loadServerConfig() {
        try {
            this.config = loadConfigFiles();

            return true;
        } catch (IOException e) {
            if (this.config == null) {
                // Create default config
                this.config = new Config();
                messageHandler.handleWarning("Falling back to default config");
            } else {
                messageHandler.handleWarning("Continuing with last working config");
            }
        }

        return false;
    }

    /**
     * Initializes the ConfigWatcher for the config file directory.
     */
    private void initializeConfigWatcher() {
        configWatcher = new ConfigWatcher(ConfigFiles.getStorageDirectory(), this::configUpdated, this::handleConfigWatcherError);

        configWatcher.start();
        lastConfigWatcherStart = System.currentTimeMillis();
    }

    /**
     * Handles the process of loading or creating (if none exists) the config files.
     * Dynamically loads the whitelisted-ips.yml and blacklisted-ips.yml files,
     * depending on if the respective features are enabled.
     *
     * @throws Exception If an error occurs when trying to create or read configs.
     */
    private Config loadConfigFiles() throws IOException {
        Config newConfig;

        Path storageDirectory = ConfigFiles.getStorageDirectory();
        if (!Files.exists(storageDirectory)) {
            Files.createDirectory(storageDirectory);
        }

        ConfigFiles serverConfig = ConfigFiles.SERVER_CONFIG;
        if (!Files.exists(serverConfig.getStoragePath())) {
            messageHandler.handleInfo("Creating " + serverConfig.getFileName() + " file");

            mapper.writeValue(Files.createFile(serverConfig.getStoragePath()).toFile(), new Config());
        }

        try {
            newConfig = mapper.readValue(serverConfig.getStoragePath().toFile(), Config.class);
        } catch (Exception e) {
            switch (e) {
                case InvalidFormatException ife -> {
                    messageHandler.handleWarning(serverConfig.getFileName() + " couldn't be parsed: "
                            + ife.getOriginalMessage());
                }

                case MarkedYAMLException mye -> {
                    messageHandler.handleWarning(serverConfig.getFileName() + " couldn't be parsed: " + mye.getProblem() + "\n"
                            + mye.getContextMark().get_snippet(0, 150));
                }

                case MismatchedInputException mie -> {
                    messageHandler.handleWarning(serverConfig.getFileName() + " is empty");
                }

                default -> {
                    messageHandler.handleError(e);
                }
            }

            throw new IOException("Failed loading config file", e);
        }

        /*
         * Load whitelist if feature is enabled
         */
        if (newConfig.isWhitelist()) {
            newConfig.setWhitelistedIPs(loadWhitelistedIPsConfig());
        }

        /*
         * Load blacklist if feature is enabled
         */
        if (newConfig.isBlacklist()) {
            newConfig.setBlacklistedIPs(loadBlacklistedIPsConfig());
        }

        return newConfig;
    }

    /**
     * Loads whitelisted IPs from "whitelisted-ips.yml" config file.
     * Creates new file if it doesn't exist yet.
     *
     * @return {@link ArrayList} containing {@link URI} entries of whitelisted IPs
     * @throws Exception If an error occurs when trying to create or read the config.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<IPAddress> loadWhitelistedIPsConfig() throws IOException {
        ConfigFiles whitelistedIPsConfig = ConfigFiles.WHITELISTED_IPS_CONFIG;

        if (!Files.exists(whitelistedIPsConfig.getStoragePath())) {
            messageHandler.handleInfo("Creating " + whitelistedIPsConfig.getFileName() + " file");

            mapper.writeValue(Files.createFile(whitelistedIPsConfig.getStoragePath()).toFile(),
                    fromIPToString(config.getWhitelistedIPs()));
        }

        ArrayList<String> ipList;
        try {
            ipList = mapper.readValue(whitelistedIPsConfig.getStoragePath().toFile(), ArrayList.class);
        } catch (Exception e) {
            switch (e) {
                case MarkedYAMLException mye -> {
                    messageHandler.handleWarning(whitelistedIPsConfig.getFileName() + " couldn't be parsed: " + mye.getProblem() + "\n"
                            + mye.getContextMark().get_snippet(0, 150));
                }

                case MismatchedInputException mie -> {
                    messageHandler.handleWarning(whitelistedIPsConfig.getFileName() + " is empty");
                }

                default -> {
                    messageHandler.handleError(e);
                }
            }

            throw new IOException("Failed loading config file", e);
        }

        return fromStringToIP(ipList);
    }

    /**
     * Loads blacklisted IPs from "blacklisted-ips.yml" config file.
     * Creates new file if it doesn't exist yet.
     *
     * @return {@link ArrayList} containing {@link URI} entries of blacklisted IPs
     * @throws Exception If an error occurs when trying to create or read the config.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<IPAddress> loadBlacklistedIPsConfig() throws IOException {
        ConfigFiles blacklistedIPsConfig = ConfigFiles.BLACKLISTED_IPS_CONFIG;

        if (!Files.exists(blacklistedIPsConfig.getStoragePath())) {
            messageHandler.handleInfo("Creating " + blacklistedIPsConfig.getFileName() + " file");

            mapper.writeValue(Files.createFile(blacklistedIPsConfig.getStoragePath()).toFile(),
                    fromIPToString(config.getBlacklistedIPs()));
        }

        ArrayList<String> ipList;
        try {
            ipList = mapper.readValue(blacklistedIPsConfig.getStoragePath().toFile(), ArrayList.class);
        } catch (Exception e) {
            switch (e) {
                case MarkedYAMLException mye -> {
                    messageHandler.handleWarning(blacklistedIPsConfig.getFileName() + " couldn't be parsed: " + mye.getProblem() + "\n"
                            + mye.getContextMark().get_snippet(0, 150));
                }

                case MismatchedInputException mie -> {
                    messageHandler.handleWarning(blacklistedIPsConfig.getFileName() + " is empty");
                }

                default -> {
                    messageHandler.handleError(e);
                }
            }

            throw new IOException("Failed loading config file", e);
        }

        return fromStringToIP(ipList);
    }

    /**
     * Converts {@link ArrayList} containing {@link URI} entries of IPs
     * to a {@link ArrayList} containing {@link String} entries of IPs.
     *
     * @param list {@link ArrayList} containing {@link URI} entries of IPs.
     * @return {@link ArrayList} containing {@link String} entries of IPs.
     */
    private ArrayList<String> fromIPToString(ArrayList<IPAddress> list) {
        ArrayList<String> strList = new ArrayList<>();

        for (IPAddress ip : list) {
            strList.add(ip.getString());
        }

        return strList;
    }

    /**
     * Converts {@link ArrayList} containing {@link String} entries of IPs
     * to a {@link ArrayList} containing {@link URI} entries of IPs.
     *
     * @param list {@link ArrayList} containing {@link String} entries of IPs.
     * @return {@link ArrayList} containing {@link URI} entries of IPs.
     */
    private ArrayList<IPAddress> fromStringToIP(ArrayList<String> list) {
        ArrayList<IPAddress> ipList = new ArrayList<>();

        for (String ip : list) {
            try {
                ipList.add(IPAddress.fromString(ip));
            } catch (InvalidIPAddressException e) {

                // TODO: Give information in which file this error occurred
                messageHandler.handleWarning("Failed parsing IP string, skipping it: \"" + ip + "\"");
            }
        }

        return ipList;
    }

    /**
     * This method gets called when a config was updated
     *
     * @param event event that was detected by the {@link ConfigWatcher}
     */
    private void configUpdated(WatchEvent<?> event) {
        Path modifiedConfig = ConfigFiles.getStorageDirectory().resolve((Path) event.context());
        String configName = modifiedConfig.getFileName().toString();

        if (!ConfigFiles.isConfig(configName)) {
            return;
        }

        messageHandler.handleInfo("\"" + configName + "\" config was updated");

        if(loadServerConfig()) {
            if (configObserver != null) {
                configObserver.configUpdated();
            }
        }
    }

    /**
     * This method gets called when an error occurred in the {@link ConfigWatcher}
     *
     * @param e Exception that was thrown
     */
    private void handleConfigWatcherError(Exception e) {
        if (System.currentTimeMillis() - lastConfigWatcherStart > 5_000) {
            restartAttempts = 0;
        }

        restartAttempts++;

        if (restartAttempts <= MAX_RESTART_ATTEMPTS) {
            messageHandler.handleError(new IOException("The ConfigWatcher thread has run into an unexpected error: " + e));
            messageHandler.handleWarning("Attempting restart " + restartAttempts + " ...");

            if (configWatcher != null) {
                configWatcher.stopWatcher();
            }

            initializeConfigWatcher();
        } else {
            messageHandler.handleError(new IOException("Failed ConfigWatcher restart after " + MAX_RESTART_ATTEMPTS + " attempts: " + e));
            messageHandler.handleWarning("Registering live updates in config files is no longer possible.");

            configWatcher.stopWatcher();
        }
    }
}
