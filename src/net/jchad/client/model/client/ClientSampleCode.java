package net.jchad.client.model.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

// Temporary client used for testing
public class ClientSampleCode {
    public static void main(String args[])
            throws Exception
    {

        // Create client socket
        Socket s = new Socket("vnos.bot.nu", 13814);

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
        System.out.println(br.readLine());
        System.out.println(br.readLine());


        // repeat as long as exit
        // is not typed at client
        while (!(str = kb.readLine()).equals("exit")) {
            // send to the server
            dos.writeBytes(str);
            dos.flush();

            // receive from the server
            str1 = br.readLine();

            System.out.println("[SERVER] " + str1);
        }

        // close connection.
        dos.close();
        br.close();
        kb.close();
        s.close();
    }
}
