package net.jchad.shared.common;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class ApiCalls {
    /**
     * Calls the github api to retrieve all releases
     * @return
     */
    public static Release[] getRepositoryReleases(URL url) throws IOException {

            //Opens  the connection to read the github json response
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
