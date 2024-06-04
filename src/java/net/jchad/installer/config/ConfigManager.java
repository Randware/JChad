package net.jchad.installer.config;


import net.jchad.installer.core.Downloader;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;



public class ConfigManager {

    private String path = ""; //where should be the files downloaded
    private String softwareToInstall = "";
    private final String githubRepo = "https://api.github.com/repos/Randware/JChad/releases/latest";
    private ConfigUtil configUtil = new ConfigUtil(Path.of(System.getProperty("user.dir")));
    public ConfigManager() {
        this(false);
    }

    public ConfigManager(boolean createIfNotExist) {
        if (configUtil.get("createIfNotExist") == null) {
            configUtil.save("createIfNotExist", createIfNotExist);
        }
    }

    /**
     * Sets the download path for the software.
     * leave empty for current path
     * @param path
     * @return instance of class for chaining
     * @throws RuntimeException if the path is invalid
     */
    public ConfigManager setDownloadPath(String path) {
        //TODO always look if the given download path has every folder created
        if (configUtil.get("downloadPath") != null && !((String) configUtil.get("downloadPath")).isEmpty() && path.isEmpty()) { //If the yml file already has a download path
            if (!isValidPath((String) configUtil.get("downloadPath"))) throw new IllegalArgumentException("The \"downloadPath: <path>\" in .yml file is invalid!"); //checks if the download path in .yml is valid
            if(getCreateIfNotExist() && isValidPath((String) configUtil.get("downloadPath"))) {
                configUtil.createDirectoriesIfNotExist(Path.of((String) configUtil.get("downloadPath")));
            }
            return this; //If it should take the defined path in the yaml file
        }
        try {
            if (path.isEmpty()) path = System.getProperty("user.dir");



            if (!ConfigUtil.isValidPath(path) || !ConfigUtil.isDirectory(Path.of(path))) {
                throw new InvalidPathException(path, "The given path is invalid");
            }

            if(getCreateIfNotExist() && isValidPath(path)) {
                configUtil.createDirectoriesIfNotExist(Path.of(path));
            }

            this.path = path;
        } catch (InvalidPathException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this;
    }

    /**
     * Sets the software to install:
     * 0 <- Server and Client software
     * 1 <- Server software
     * 2 <- Client software
     * @param number
     * @throws
     * @return
     */
    public ConfigManager setSoftwareToInstall(int number) {
        softwareToInstall = switch(number) {
            case 0 -> "both";
            case 1 -> "server";
            case 2 -> "client";
            default ->  throw new IllegalArgumentException("The given number that indicates which software to install is invalid!");
        };

        return this;
    }

    public boolean process() {
        try {
            path = ConfigUtil.unifyPath(path);



            if (!path.equals("")) {
                configUtil.save("downloadPath", path);
            }
            if (softwareToInstall.isEmpty()) throw new IllegalArgumentException("Software not set that should be installed. Methode setSoftwareToInstall must be called before");
            else configUtil.save("softwareToInstall", softwareToInstall);
            Downloader.download(githubRepo, Path.of(path), softwareToInstall);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getCreateIfNotExist() {
        try {
            if (configUtil.get("createIfNotExist") != null) {
                if ((Boolean) configUtil.get("createIfNotExist")) {
                    return true;
                } else if ((Boolean) configUtil.get("createIfNotExist")){
                    return false;
                }
            }
            return false; //not set
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("createIfNotExist: <true/false> is not a boolean in the .yml file!");
        }
    }

    public Object getValue(String value) {
        return configUtil.get(value);
    }

    public boolean isValidPath(String filePath) {
        if (ConfigUtil.isValidPath(filePath)) {
            if (!getCreateIfNotExist()) {
              return  Files.exists(Path.of(filePath));
            } else {
                return true;
            }
        } else {
            return false;
        }

    }
}