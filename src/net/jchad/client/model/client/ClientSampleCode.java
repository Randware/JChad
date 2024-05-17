package net.jchad.client.model.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Temporary client used for testing
public class ClientSampleCode {
    public static void main(String[] args) {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 13814);

            // Obtain input and output streams
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // Wrap the streams with readers and writers for easier communication
            PrintWriter out = new PrintWriter(outputStream, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            // Start a thread for listening to server responses
            Thread listenerThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("Server response: " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            listenerThread.start();

            // Scanner for user input
            Scanner scanner = new Scanner(System.in);
            StringBuilder builder = new StringBuilder();
            // Start sending user input to the server
            while (true) {

                    out.println(scanner.nextLine());
                    out.flush();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
