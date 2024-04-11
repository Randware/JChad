package net.jchad.server.model.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.jchad.server.model.common.ThrowingRunnable;
import net.jchad.server.model.error.MessageHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/*
    TODO: Implement validation for config files -> validate file before live updating
        - Catch Jackson format exception
    TODO: Fix live reloading multiple time
        - check if modified file is actual config file (or just temp file, user created file, etc.)
    TODO: Inform ConfigManager which WatchEvent has occurred
        - can check if the updated file is an actual config file (mentioned in previous entry)
        - can also log which file was changed
    TODO: Fix bug, where the program "fails" updating the config when creating it
        - Only happens if config directory not created yet, only happens in jar outside of IDE
            - Creating config directory before running ConfigWatcher on it seems to fix it
    DONE: Fix live reloading multiple times -> pause ConfigWatcher when saving configs from program
 */
public class ConfigManager {
    private final String configSavePath = "./configs/";

    private final String serverConfigName = "server-config.yml";
    private final String whitelistedIPsConfigName = "whitelisted-ips.yml";
    private final String blacklistedIPsConfigName = "blacklisted-ips.yml";

    private final ObjectMapper mapper;

    private ConfigWatcher configWatcher;
    private ConfigObserver configObserver;
    private MessageHandler messageHandler;
    private Config config;

    public ConfigManager(MessageHandler messageHandler) {
        this(messageHandler, null);
    }

    public ConfigManager(MessageHandler messageHandler, ConfigObserver configObserver) {
        this.mapper = new ObjectMapper(new YAMLFactory());

        this.messageHandler = messageHandler;
        this.configObserver = configObserver;

        // Run the ConfigWatcher on the config file directory if not running already. When changes are detected, call configUpdated()
        try {
            configWatcher = new ConfigWatcher(Path.of(configSavePath), this::configUpdated);
        } catch (IOException e) {
            messageHandler.handleError(new IOException("The ConfigWatcher thread has unexpectedly stopped, live updating the config no longer works", e));
        }

        configWatcher.start();

        this.config = null;
    }

    /*
    Handling the exception is the callers job, since this enables the caller to use a previous config if loading a new one fails.
     */
    public Config getConfig() throws Exception {
        loadServerConfig();

        return config;
    }

    private void loadServerConfig() throws Exception {
        Path configPath = Path.of(configSavePath + serverConfigName);

        if(!Files.exists(Path.of(configSavePath))) {
            Files.createDirectory(Path.of(configSavePath));
        }

        if(!Files.exists(configPath)) {
            runUnsupervised(() -> {
                mapper.writeValue(Files.createFile(configPath).toFile(), new Config());
            });
        }

        this.config = mapper.readValue(configPath.toFile(), Config.class);

        // Load whitelist if feature is enabled
        if(config.isWhitelist()) {
            try {
                config.setWhitelistedIPs(loadWhitelistedIPsConfig());
            } catch (IOException e) {
                messageHandler.handleError(e);
            }
        }

        // Load blacklist if feature is enabled
        if(config.isBlacklist()) {
            try {
                config.setBlacklistedIPs(loadBlacklistedIPsConfig());
            } catch (IOException e) {
                messageHandler.handleError(e);
            }
        }
    }

    private ArrayList<URI> loadWhitelistedIPsConfig() throws Exception {
        Path whitelistedIPsPath = Path.of(configSavePath + whitelistedIPsConfigName);

        if(!Files.exists(whitelistedIPsPath)) {
            runUnsupervised(() -> {
                mapper.writeValue(Files.createFile(whitelistedIPsPath).toFile(), fromURIToString(config.getWhitelistedIPs()));
            });
        }

        return fromStringToURI(mapper.readValue(whitelistedIPsPath.toFile(), new TypeReference<>() {}));
    }

    private ArrayList<URI> loadBlacklistedIPsConfig() throws Exception {
        Path blacklistedIPsPath = Path.of(configSavePath + blacklistedIPsConfigName);

        if(!Files.exists(blacklistedIPsPath)) {
            runUnsupervised(() -> {
                mapper.writeValue(Files.createFile(blacklistedIPsPath).toFile(), fromURIToString(config.getBlacklistedIPs()));
            });
        }

        return fromStringToURI(mapper.readValue(blacklistedIPsPath.toFile(), new TypeReference<>() {}));
    }

    private ArrayList<String> fromURIToString(ArrayList<URI> list) {
        ArrayList<String> strList = new ArrayList<>();

        for(URI ip : list) {
            strList.add(ip.toString());
        }

        return strList;
    }

    private ArrayList<URI> fromStringToURI(ArrayList<String> list) {
        ArrayList<URI> uriList = new ArrayList<>();

        for(String ip : list) {
            try {
                uriList.add(new URI(ip));
            } catch (URISyntaxException e) {
                // TODO: Give information in which file this error occurred
                messageHandler.handleWarning("Failed parsing IP string: " + ip);
            }
        }

        return uriList;
    }

    private void runUnsupervised(ThrowingRunnable code) throws Exception {
        configWatcher.pauseWatching();

        code.run();

        configWatcher.continueWatching();
    }

    private void configUpdated() {
        if(configObserver != null) {
            configObserver.configUpdated();
        }
    }
}
