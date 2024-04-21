package net.jchad.installer.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private String defaultYAMLName = "installer.yml";
    private Path pathToYAMLFile;



    protected ConfigUtil(Path pathOfConfigDirectory, String name) {
        System.out.println(pathOfConfigDirectory.toAbsolutePath());
        Path.of(pathOfConfigDirectory.toAbsolutePath() + name + ".yml");

    }

    /**
     * Constructor for creating the yaml file.
     * If the path already has a yaml file nothing will get created
     * @param config
     * @throws InvalidPathException if the given Path isn't valid
     */
    protected ConfigUtil(Path config) throws InvalidPathException{

        //unifies the Path
        config = unifyPath(config.toAbsolutePath());

        //Adds the file-ending if the user inputs a path without a filename at the end. like:
        // C:/user/admin/desktop  <-- adds the defaultYAMLName there
        if (isDirectory(config)) config = config.resolve(defaultYAMLName);


        if (!Files.exists(config)) {
            try {



                //If there is no file at the end of the given path a default name for the .yaml file gets "added"
                if (isDirectory(config)){
                    config = config.resolve(defaultYAMLName);
                }

                //creates Files
                Files.createFile(config);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }


        pathToYAMLFile = config;

    }



    /**
     *
     * @param path
     * @return  false - it is a path. false - it isn't a path.
     */
    protected static boolean isValidPath(String path) {
        try {
            Path.of(path);
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * This checks if a path THAT does not exist is a directory.
     * The problem is that Files.isDirectory only can tell if something is a directory if it exists
     * @return   if it is a directory or not
     */
    public static boolean isDirectory(final Path path) {
        String newPath = unifyPath(path.normalize().toAbsolutePath()).toString();
        String[] newPathArray = newPath.split("/");
        return !newPathArray[newPathArray.length - 1].contains(".");
    }

    /**
     * replaces all back-slashes "\" with forward-slashes "/"
     * @param path
     * @return
     */
    public static Path unifyPath(Path path) {
        return Path.of(unifyPath(path.toString()));
    }

    public static String unifyPath(String path) {
        path = path.replace("\\\\","/");
        return path.replace("\\", "/");
    }

    /**
     * gets the value of the given name
     * name = "value"
     * @param name
     * @return
     */
    protected Object get(String name) {
        Map<String, Object> map = loadMap();
        return map.get(name);
    }

    /**
     *  Creates the directories of the path of they don't exist
     * C:/user/DariOS/documents  <--- This path exists but the input is this
     * C:/user/DariOS/documents/jars/test/   <--- now /jars/test/ gets created
     *
     *
     * @param path The path which directories should get created if the not exist
     * @return If the directories got created. True for yes and false for error/already exists
     */
    protected boolean createDirectoriesIfNotExist(Path path) {
        try {
            //creates directories if the directories in the path don't exist
            Path parentPath = (isDirectory(path) ? path : path.getParent());
            if (parentPath != null && !Files.exists(parentPath)) {
                Files.createDirectories(parentPath);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * checks if the name is in the .yaml file
     * name = "value"
     * @param name
     * @return
     */
    protected boolean containsVariable(String name) {
        return loadMap().containsKey(name);
    }

    /**
     * Saves data to the .yaml file in this form
     * name = "value"
     *
     * @param name the name of the data that gets saved
     * @param value the value of the data that gets saved
     */
    protected void save(String name, Object value) {
        try {
            Map<String, Object> map = loadMap();
            if (map == null) {
                map = new HashMap<>(); // Initialize a new map if loadMap() returnss null
            }
            map.putAll(loadMap());
            map.put(name, value);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(pathToYAMLFile.toFile(), map);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("YAML didn't get saved!");
        }
    }

    /**
     * Loads the keys + values from the .yaml file
     * @return the Map containing the keys/values OR a empty HashMap if the file is empty or doesn't exist
     */
    private Map<String, Object> loadMap() {
        try {
            if (!Files.exists(pathToYAMLFile) || Files.size(pathToYAMLFile) == 0) { //If the file is empty this prevents an Exception
                return new HashMap<>();
            }
            return objectMapper.readValue(pathToYAMLFile.toFile(), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
