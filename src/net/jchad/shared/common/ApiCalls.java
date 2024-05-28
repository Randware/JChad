package net.jchad.shared.common;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * This class ({@link ApiCalls}) provides functionality to make basic api calls to the GitHub api.
 */
public class ApiCalls {
    /**
     * Calls the GitHub api to retrieve all releases.
     * The releases get returned as an {@link Release Realease[]} array.
     * The {@link Release} class stores many variables from the api response from GitHub.
     * <b>This methode throws exceptions!<u>Make sure to handle them!</u></b>
     *
     * @throws IOException
     * @return An array that represents the releases on the official GitHub page
     */
    public static Release[] getRepositoryReleases(URL url) throws IOException {

            //Opens the connection to read the github json response
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            //Reads the response
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String currentLine;
            while ((currentLine = input.readLine()) != null) {
                response.append(currentLine);
            }
            Gson gson = new Gson();
            return gson.fromJson(response.toString(), Release[].class);

    }
}
