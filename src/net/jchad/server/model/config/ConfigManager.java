package net.jchad.server.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException;
import net.jchad.server.model.common.ThrowingRunnable;
import net.jchad.server.model.error.MessageHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*

    TODO: Fix bug, where the program "fails" updating the config when creating it
        - Only happens if config directory not created yet, only happens in jar outside of IDE
            - Creating config directory before running ConfigWatcher on it seems to fix it
    TODO: Maybe don't dynamically load whitelisted and blacklisted files
    TODO: Rewrite structure, so error handling and logging becomes easier and less hacky
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

/**
 * This class manages the multiple server configuration files.
 * Loading, saving and watching the files  for changes is all managed here.
 */
public class ConfigManager {
    /**
     * Stores the save path for the configuration files.
     */
    private final String configSavePath = "./configs/";

    /**
     * Stores a key-value pair for each config file, containing the configs specifier name and its file name.
     * <br>
     * <br>
     * Format:
     * <br>
     * "[CONFIG SPECIFIER]" : "[CONFIG FILE NAME]"
     * <br>
     * <br>
     * "serverConfig" : "server-config.yml"
     * <br>
     * "whitelistedIPsConfig" : "whitelisted-ips.yml"
     * <br>
     * "blacklistedIPsConfig" : "blacklisted-ips.yml"
     */
    private HashMap<String, String> configs = new HashMap<>(Map.of(
            "serverConfig", "server-config.yml",
            "whitelistedIPsConfig", "whitelisted-ips.yml",
            "blacklistedIPsConfig", "blacklisted-ips.yml"
    ));

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
     *
     * @param messageHandler {@link MessageHandler} implementing class instance responsible for handling errors,
     *                                             warnings and logs
     */
    public ConfigManager(MessageHandler messageHandler) {
        this(messageHandler, null);
    }

    /**
     *
     * @param messageHandler {@link MessageHandler} implementing class instance responsible for handling errors,
     *                                             warnings and logs
     * @param configObserver {@link ConfigObserver} implementing class instance,
     *                                             which gets notified when config file changes are detected.
     */
    public ConfigManager(MessageHandler messageHandler, ConfigObserver configObserver) {
        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

        this.messageHandler = messageHandler;
        this.configObserver = configObserver;

        /*
         * Run the ConfigWatcher on the config file directory. When changes are detected,
         * call configUpdated(event), "event" being the WatchEvent<?> that got registered.
         */
        try {
            configWatcher = new ConfigWatcher(Path.of(configSavePath), (event) -> {
                configUpdated(event);
            });
        } catch (IOException | UncheckedIOException e) {
            messageHandler.handleError(new IOException("The ConfigWatcher thread has unexpectedly stopped, live updating the config no longer works", e));
        }

        configWatcher.start();

        this.config = null;
    }

    /**
     * Loads config from file and returns it.
     *
     * @return {@link Config} that got loaded
     * @throws Exception If loading the config fails
     */
    public Config getConfig() throws Exception {
        loadServerConfig();

        return config;
    }

    /**
     * Handles the process of loading or creating (if none exists) the config files.
     * Dynamically loads the whitelisted-ips.yml and blacklisted-ips.yml files,
     * depending on if the respective features are enabled.
     *
     *
     * @throws Exception If an error occurs when trying to create or read configs.
     */
    private void loadServerConfig() throws Exception {
        Path serverConfigPath = Path.of(configSavePath + configs.get("serverConfig"));

        if(!Files.exists(Path.of(configSavePath))) {
            Files.createDirectory(Path.of(configSavePath));
        }

        if(!Files.exists(serverConfigPath)) {
            messageHandler.handleInfo("Creating " + configs.get("serverConfig") + " file");

            mapper.writeValue(Files.createFile(serverConfigPath).toFile(), new Config());
        }

        try {
            this.config = mapper.readValue(serverConfigPath.toFile(), Config.class);
        } catch (InvalidFormatException e) {
            messageHandler.handleWarning(configs.get("serverConfig") + " couldn't be parsed: "
                            + e.getOriginalMessage());

            if(this.config == null) {
                this.config = new Config();
                messageHandler.handleWarning("Falling back to default config");
            } else {
                throw new IOException(configs.get("serverConfig") + " is invalid");
            }
        } catch (MismatchedInputException e) {
            messageHandler.handleWarning(configs.get("serverConfig") + " is empty");
        }

        /*
         * Load whitelist if feature is enabled
         */
        if(config.isWhitelist()) {
            config.setWhitelistedIPs(loadWhitelistedIPsConfig());
        }

        /*
         * Load blacklist if feature is enabled
         */
        if(config.isBlacklist()) {
            config.setBlacklistedIPs(loadBlacklistedIPsConfig());
        }
    }

    /**
     * Loads whitelisted IPs from "whitelisted-ips.yml" config file.
     * Creates new file if it doesn't exist yet.
     *
     * @return {@link ArrayList} containing {@link URI} entries of whitelisted IPs
     * @throws Exception If an error occurs when trying to create or read the config.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<InetAddress> loadWhitelistedIPsConfig() throws Exception {
        Path whitelistedIPsPath = Path.of(configSavePath + configs.get("whitelistedIPsConfig"));

        if(!Files.exists(whitelistedIPsPath)) {
            messageHandler.handleInfo("Creating " + configs.get("whitelistedIPsConfig") + " file");

            mapper.writeValue(Files.createFile(whitelistedIPsPath).toFile(), fromURIToString(config.getWhitelistedIPs()));
        }

        ArrayList<String> ipList = new ArrayList<>();
        try {
            ipList = mapper.readValue(whitelistedIPsPath.toFile(), ArrayList.class);
        } catch (MarkedYAMLException e) {
            messageHandler.handleWarning(configs.get("blacklistedIPsConfig") + " couldn't be parsed: " + e.getProblem() + "\n"
                        + e.getContextMark().get_snippet(0, 150));

        } catch (MismatchedInputException e) {
            messageHandler.handleWarning(configs.get("whitelistedIPsConfig") + " is empty");
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
    private ArrayList<InetAddress> loadBlacklistedIPsConfig() throws Exception {
        Path blacklistedIPsPath = Path.of(configSavePath + configs.get("blacklistedIPsConfig"));

        if(!Files.exists(blacklistedIPsPath)) {
            messageHandler.handleInfo("Creating " + configs.get("blacklistedIPsConfig") + " file");

            mapper.writeValue(Files.createFile(blacklistedIPsPath).toFile(), fromURIToString(config.getBlacklistedIPs()));
        }

        ArrayList<String> ipList = new ArrayList<>();
        try {
            ipList = mapper.readValue(blacklistedIPsPath.toFile(), ArrayList.class);
        } catch (MarkedYAMLException e) {
            messageHandler.handleWarning(configs.get("blacklistedIPsConfig") + " couldn't be parsed: " + e.getProblem() + "\n"
                        + e.getContextMark().get_snippet(0, 150));

        } catch (MismatchedInputException e) {
            messageHandler.handleWarning(configs.get("blacklistedIPsConfig") + " is empty");
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
    private ArrayList<String> fromURIToString(ArrayList<InetAddress> list) {
        ArrayList<String> strList = new ArrayList<>();

        for(InetAddress ip : list) {
            strList.add(ip.getHostAddress());
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
    private ArrayList<InetAddress> fromStringToIP(ArrayList<String> list) {
        ArrayList<InetAddress> ipList = new ArrayList<>();

        for(String ip : list) {
            try {
                ipList.add(InetAddress.getByAddress(ip.getBytes()));
            } catch (UnknownHostException e) {



                // TODO: Give information in which file this error occurred
                messageHandler.handleWarning("Failed parsing IP string: \"" + ip + "\"");
            }
        }

        return ipList;
    }


    /**
     * Allows a piece of code to be executed without being registered by the {@link ConfigWatcher}.
     *
     * @param code Code which gets executed without being registered by the {@link ConfigWatcher}
     * @throws Exception If an error occurs in the code
     */
    @Deprecated
    private void runUnsupervised(ThrowingRunnable code) throws Exception {
        configWatcher.pauseWatching();
        System.out.println("Running unsupervised code");

        code.run();

        configWatcher.continueWatching();
    }

    /**
     * Called when the {@link ConfigWatcher} registers a modification event in the config directory.
     *
     * @param event {@link WatchEvent} containing the event registered by the {@link ConfigWatcher}
     */
    private void configUpdated(WatchEvent<?> event) {
        Path modifiedConfig = Path.of(configSavePath).resolve((Path) event.context());
        String configName = modifiedConfig.getFileName().toString();

        if(!configs.containsValue(configName)) {
            return;
        }

        if(configObserver != null) {
            messageHandler.handleInfo("\"" + configName + "\" config was updated");

            configObserver.configUpdated();
        }
    }
}
