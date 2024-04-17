package net.jchad.server.model.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException;
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

public class ConfigManagerRewrite {
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
    public ConfigManagerRewrite(MessageHandler messageHandler) {
        this(messageHandler, null);
    }

    /**
     *
     * @param messageHandler {@link MessageHandler} implementing class instance responsible for handling errors,
     *                                             warnings and logs
     * @param configObserver {@link ConfigObserver} implementing class instance,
     *                                             which gets notified when config file changes are detected.
     */
    public ConfigManagerRewrite(MessageHandler messageHandler, ConfigObserver configObserver) {
        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

        this.messageHandler = messageHandler;
        this.configObserver = configObserver;

        this.config = new Config();

        try {
            loadServerConfig();
        } catch (Exception e) {
            messageHandler.handleWarning("Failed loading server config, continuing with default config");
        }

        try {
            configWatcher = new ConfigWatcher(Path.of(configSavePath), (event) -> {
                configUpdated(event);
            });
        } catch (UncheckedIOException e) {
            messageHandler.handleError(new IOException("The ConfigWatcher thread has unexpectedly stopped, live updating the config no longer works", e));
        }

        configWatcher.start();
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

            throw new Exception("Couldn't successfully update the config");
        } catch (MismatchedInputException e) {
            messageHandler.handleWarning(configs.get("serverConfig") + " is empty");

            throw new Exception("Couldn't successfully update the config");
        } catch (Exception e) {
            messageHandler.handleError(e);

            throw new Exception("Couldn't successfully update the config");
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

            throw new Exception("Couldn't successfully update the config");
        } catch (MismatchedInputException e) {
            messageHandler.handleWarning(configs.get("whitelistedIPsConfig") + " is empty");

            throw new Exception("Couldn't successfully update the config");
        } catch (Exception e) {
            messageHandler.handleError(e);

            throw new Exception("Couldn't successfully update the config");
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

            throw new Exception("Couldn't successfully update the config");
        } catch (MismatchedInputException e) {
            messageHandler.handleWarning(configs.get("blacklistedIPsConfig") + " is empty");

            throw new Exception("Couldn't successfully update the config");
        } catch (Exception e) {
            messageHandler.handleError(e);

            throw new Exception("Couldn't successfully update the config");
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

    public void configUpdated(WatchEvent<?> event) {
        Path modifiedConfig = Path.of(configSavePath).resolve((Path) event.context());
        String configName = modifiedConfig.getFileName().toString();

        if(!configs.containsValue(configName)) {
            return;
        }

        messageHandler.handleInfo("\"" + configName + "\" config was updated");

        try {
            loadServerConfig();
            messageHandler.handleInfo("Successfully loaded new config");

            if(configObserver != null) {
                configObserver.configUpdated();
            }
        } catch (Exception e) {
            messageHandler.handleWarning("Failed loading new config");
        }
    }
}
