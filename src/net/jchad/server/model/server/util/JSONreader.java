package net.jchad.server.model.server.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.jchad.server.model.server.ServerThread;

import java.io.IOException;
import java.util.Scanner;

public class JSONreader {
private final Gson gson = new Gson();
private final ServerThread serverThread;
private final Scanner scanner;

public JSONreader(ServerThread serverThread) throws IOException {
    this.serverThread = serverThread;
    this.scanner = new Scanner(serverThread.getSocket().getInputStream());

}

public String readNext() {
    StringBuilder jsonString = new StringBuilder();
    do {
        break;
    } while (true);
    return jsonString.toString();
}

public boolean hasNext() {
    return scanner.hasNext();
}

public void close() {
    scanner.close();
}

}
