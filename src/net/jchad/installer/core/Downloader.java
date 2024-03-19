package net.jchad.installer.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.BarStatus;
import net.jchad.installer.core.progressBar.BarUpdater;
import net.jchad.installer.exceptions.InvalidArgumentException;
import net.jchad.installer.serializable.RepoFile;

import java.io.*;
import java.net.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Downloader extends BarUpdater{

        private final Gson gson = new Gson();



   public Downloader() throws InstantiationException {
       super("RepoDownloader", null, new LinkedHashSet<>());
   }

    /**
     *
     * @param link
     * @param downloadPath
     * @param softwareToInstall 0 -> both; 1 -> server; 2 -> client;
     * @return
     */
   public static boolean download(String link, Path downloadPath, String softwareToInstall) {

       try {
           Downloader downloader = new Downloader();
           URI uriLink = downloader.stringToURI(link);
                   JsonObject jsonObject = downloader.getJSON(uriLink.toURL());
                   if (jsonObject == null) return false;
           List<RepoFile> repoFileList = downloader.getFiles(jsonObject).stream()
                   .filter((i) -> i.name().contains("client") || i.name().contains("server"))
                   .collect(Collectors.toList());


               repoFileList.removeIf(i -> i.name().contains(switch (softwareToInstall) {
                   case "both" -> "none";
                   case "server" -> "client";
                   case "client" -> "server";
                   default -> throw new IllegalArgumentException(softwareToInstall + " has to be both, client or server!");
               }));
           downloader.install(downloadPath, repoFileList.toArray(new RepoFile[0]));
           return true;
       } catch (InstantiationException | MalformedURLException | InvalidArgumentException | IllegalArgumentException e) {
           e.printStackTrace();
       }
       return false;
   }



    private JsonObject getJSON(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                result.append(currentLine);
            }

            return gson.fromJson(result.toString(), JsonObject.class);

        } catch (IOException e) {
            BarStatus barStatus = BarStatus.PROGRESS_FAILED;
            barStatus.setException(e);
            super.getBar().setBarStatus(barStatus);
            return null;
        }
    }


    private List<RepoFile> getFiles(JsonObject jo) {
       List<RepoFile> retValue = new ArrayList<>();
       JsonArray jsonArray = jo.getAsJsonArray("assets");
       for  (int i = 0; i < jsonArray.size(); i++) {
           RepoFile repoFile = gson.fromJson(jsonArray.get(i), RepoFile.class);
           retValue.add(repoFile);
       }

        return retValue;
    }

    private boolean install(Path path, RepoFile... files) throws InvalidArgumentException {

            if (!Files.isDirectory(path))  {
                throw  new InvalidArgumentException("%s is not a directory!".formatted(path.toString()), "Please make sure that the provided path is a directory");
            }
            long sumFileSize = 0;
            for (RepoFile repoFile : files) { //calculates
                sumFileSize += repoFile.size();
            }

            super.setBar(new Bar(sumFileSize, this, 0, 0, BarStatus.PROGRESS_START));
            for (RepoFile file : files) {
                URI uri = stringToURI(file.browser_download_url());
                try (OutputStream output = Files.newOutputStream(Path.of(path.toString(), file.name()));
                     BufferedInputStream inputStream = new BufferedInputStream(uri.toURL().openStream())) {
                    byte[] dataBuffer = new byte[1024];
                    int readBytes;
                    while ((readBytes = inputStream.read(dataBuffer)) != -1) {
                        super.getBar().addProgress(readBytes).setBarStatus(BarStatus.PROGRESS_UPDATE);
                        output.write(dataBuffer, 0, readBytes);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            super.getBar().setBarStatus(BarStatus.PROGRESS_END);
            return true;

    }


    private URI stringToURI(String strUri) {
      return URI.create(strUri);
    }


}
