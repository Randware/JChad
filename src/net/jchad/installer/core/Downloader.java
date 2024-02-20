package net.jchad.installer.core;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Downloader {

    public void getServer(Path path) {
        if (!Files.isDirectory(path)) throw new IllegalArgumentException("Path must be a Directory!");

    }

}
