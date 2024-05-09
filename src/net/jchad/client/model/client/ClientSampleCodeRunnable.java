package net.jchad.client.model.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSampleCodeRunnable implements Runnable {
    @Override
    public void run() {
        try {
            // Create client socket
            Socket s = new Socket("81.217.113.13", 12312);

            // to send data to the server
            DataOutputStream dos
                    = new DataOutputStream(
                    s.getOutputStream());

            // to read data coming from the server
            BufferedReader br
                    = new BufferedReader(
                    new InputStreamReader(
                            s.getInputStream()));

            // to read data from the keyboard
            BufferedReader kb
                    = new BufferedReader(
                    new InputStreamReader(System.in));
            String str, str1;


            // repeat as long as exit
            // is not typed at client
            while (true) {
                Thread.currentThread().sleep(500);
                // send to the server
                dos.writeBytes(Thread.currentThread().getName() + "\n");

                // receive from the server
                str1 = br.readLine();

                System.out.println("[SERVER] " + str1);
            }

            // close connection.
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
