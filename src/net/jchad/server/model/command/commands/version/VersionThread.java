package net.jchad.server.model.command.commands.version;

import com.google.gson.Gson;
import net.jchad.server.model.error.MessageHandler;
import net.jchad.shared.common.ApiCalls;
import net.jchad.shared.common.Release;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class VersionThread implements Runnable{
    private final URL url;
    private final MessageHandler messageHandler;
    private final String version;
    private Release[] releases = null;

    public VersionThread(URL url, MessageHandler messageHandler, String version) {
        this.url = url;
        this.messageHandler = messageHandler;
        this.version = version;
    }

    /**
     * Calls all methods to retrieve the needed information
     */
    @Override
    public void run() {
        try {
            releases = ApiCalls.getRepositoryReleases(url);
            messageHandler.handleInfo("""
                %s
                %s
                %s
                """.formatted(compareCurrentVersion(), getCurrentVersion(), getThanks()));
        } catch (IOException e) {
            messageHandler.handleError(new Exception("Error while trying to fetch GitHub information: " + e.getMessage()));
        }


    }


    /**
     * Checks if the current version iis the newest one
     * @return
     */
    public String compareCurrentVersion() {
        StringBuilder versionInfo = new StringBuilder();
        boolean versionFound = false;
        for (int i = 0; i < releases.length; i++) {
            if (this.version.equals(releases[i].getTag_name())) {
                if (i == 0) { //Newest versions are always at the beginning of the array
                    versionInfo.append("You are running the latest version of JChad!");
                    versionFound = true;
                } else {
                    versionInfo.append("You are running an older version of JChad\n");
                    versionInfo.append("The version you are running is %d release(s) behind. We recommend, to update to the newest version: %s".formatted(i, releases[0].getTag_name()));
                    versionFound = true;
                }
            }
        }
        if (!versionFound) {
            versionInfo.append("You are running an unknown version of JChad (%s).\n".formatted(version));
            versionInfo.append("If you downloaded the JChad server software from the official repo open an issue there");
        }
        return versionInfo.toString();
    }

    /**
     * Displays information about the running version
     * @return
     */
    public String getCurrentVersion() {
        StringBuilder currentVersion = new StringBuilder();
        Release currentRelease = null;
        for (Release release : releases) {
            if (version.equals(release.getTag_name())) {
                currentRelease = release;
            }
        }
        if (currentRelease != null) {
            currentVersion.append("Information about current version:");
            currentVersion.append("\nName: ").append(currentRelease.getName())
                    .append("\nVersion: ").append(currentRelease.getTag_name())
                    .append("\nDescription: ").append(currentRelease.getBody())
                    .append("\nUploaded at: ").append(convertGitHubApiDateTime(currentRelease.getPublished_at()));


        } else {
            currentVersion.append("There are no informations available for this unknown version of JChad server");
        }
        return currentVersion.toString();
    }

    public String getThanks() {
        return "Thanks for using JChad!\nWe would appreciate it if you star our GitHub repository: https://github.com/Randware/JChad";
    }

    /**
     * This converts the uploaded date from github to a human-readable date
     * @param dateFromGitHub The data from github
     * @return A readable date
     */
    public String convertGitHubApiDateTime(String dateFromGitHub) {

       try {
           // Parse the string into a ZonedDateTime object in UTC
           ZonedDateTime utcDateTime = ZonedDateTime.parse(dateFromGitHub);

           // Convert UTC ZonedDateTime to the system default timezone
           ZonedDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault());

           // Format the local ZonedDateTime into a more readable string
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
           return localDateTime.format(formatter);

       } catch (Exception e) {
           return "Unknown";
       }
    }


}
