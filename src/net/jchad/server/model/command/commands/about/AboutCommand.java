package net.jchad.server.model.command.commands.about;

import com.google.gson.Gson;
import net.jchad.server.model.command.commands.BaseCommand;
import net.jchad.server.model.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.*;

public class AboutCommand extends BaseCommand  implements Callable<Release[]> {
    private static final URL repoURL;
    static {
        try {
            repoURL =  new URL("https://api.github.com/repos/randware/jchad/releases/latest");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Special mega ultra rare exception! The repo url in the about command is malformed", e);
        }
    }

    @Override
    public void execute(Server server, ArrayList<String> args) {

        server.getMessageHandler().handleInfo(infoBuilder(server));


    }

    public String infoBuilder(Server server) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Release[]> releasesFuture = executor.submit(this);
        try {
            Release[] releases = releasesFuture.get();
            if (releases == null) {
                server.getMessageHandler().handleError(new Exception("An error occurred while trying to fetch all versions"));
            } else {

                return
                        allVersionsInfoBuilder(server, releases) + "\n"
                        + currentVersionInfoBuilder(server, releases);
            }
        } catch (InterruptedException | ExecutionException e) {
            server.getMessageHandler().handleError(new Exception("An error occurred while trying to fetch all versions", e));
        }
        return "An error occurred while trying to fetch the latest data";
    }

    public String allVersionsInfoBuilder(Server server, Release[] releases) {
        StringBuilder version = new StringBuilder();
        boolean versionFound = false;
        for (int i = 0; i < releases.length; i++) {
            if (server.getVersion().equals(releases[i].getTag_name())) {
                if (i == 0) { //Newest versions are always at the beginning of the array
                    version.append("You are running the latest version of JChad!");
                    versionFound = true;
                } else {
                    version.append("You are running an older version of JChad\n");
                    version.append("The version you are running is %d releases behind. We recommend, to update tp the newest version: %s".formatted(i, releases[0].getTag_name()));
                    versionFound = true;
                }
            }
        }
        if (!versionFound) {
            version.append("You are running an unknown version of JChad (%s).\n".formatted(server.getVersion()));
            version.append("If this is downloaded from the official GitHub open an Issue");
        }
        return version.toString();
    }

    public String currentVersionInfoBuilder(Server server, Release[] releases) {
        StringBuilder currentVersion = new StringBuilder();
        Release currentRelease = null;
        for (Release release : releases) {
            if (server.getVersion().equals(release.getTag_name())) {
                currentRelease = release;
            }
        }
        if (currentRelease != null) {
            currentVersion.append("Information about current version:\n");
            currentVersion.append("Name: ")
                    .append(currentRelease.getName()).append("\n Version: ")
                    .append(currentRelease.getTag_name()).
                    append("\n").
                    append("Uploaded at: ").append(currentRelease.getPublished_at());


        } else {
            currentVersion.append("There are no information for this unknown version available");
        }
        return currentVersion.toString();
    }

    public String thankerInfoBuilder() {
        return "\n Thank you for using JChad!";
    }


    @Override
    public Release[] call() throws Exception {
        try {
            //Opens  the connection ot read the github json response
            HttpURLConnection connection = (HttpURLConnection) repoURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            //Reads the response
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String currentLine;
            response.append("[");
            while ((currentLine = input.readLine()) != null) {
                response.append(currentLine);
            }
            response.append("]");
            System.out.println(response);
            Gson gson = new Gson();
            return gson.fromJson(response.toString(), Release[].class);
        } catch (Exception e) {
            return null;
        }
    }

    /*private static Release[] getRepositoryReleases(URL urlTOReleases) {
        try {
            //Opens  the connection ot read the github json response
            HttpURLConnection connection = (HttpURLConnection) urlTOReleases.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            //Reads the response
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String currentLine;
            response.append("[");
            while ((currentLine = input.readLine()) != null) {
                response.append(currentLine);
            }
            response.append("]");
            System.out.println(response);
            Gson gson = new Gson();
            return gson.fromJson(response.toString(), Release[].class);
        } catch (Exception e) {
            return null;
        }
    }*/
}
