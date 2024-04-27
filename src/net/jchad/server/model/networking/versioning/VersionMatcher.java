package net.jchad.server.model.networking.versioning;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import net.jchad.server.model.networking.InvalidPacketException;
import net.jchad.server.model.server.ServerThread;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public class VersionMatcher {
    /**
     * Gson gson = new Gson();
     *
     *         int retries = 3;
     *         for (int i = 0; i < retries; i++) {
     *             try {
     *
     *                 while (serverThread.getJsonReader().hasNext()) {
     *                     Thread.currentThread().sleep(100);
     *                     JsonToken jsonToken = serverThread.getJsonReader().peek();
     *                     if (JsonToken.BEGIN_OBJECT.equals(jsonToken)) {
     *                             serverThread.getJsonReader().beginObject();
     *                             ClientVersionMatcherPacket vmp = gson.fromJson(serverThread.getJsonReader(), ClientVersionMatcherPacket.class);
     *                             System.out.println(vmp);
     *                             serverThread.getJsonReader().endObject();
     *                             serverThread.getJsonReader().skipValue();
     *                     }
     *
     *                 }
     *
     *             } catch (JsonSyntaxException | MalformedJsonException jse) {
     *
     *                 System.out.println("Invalid");
     *                 serverThread.getMessageHandler().handleError(new InvalidPacketException("The received json packet cannot be read from "
     *                         + serverThread.getRemoteAddress() + ".The connection will be terminated" +
     *                         ((i == retries - 1) ? " now" : (" after " + (retries - (i + 1)) + " more failed attempt(s)")), jse));
     *
     *                 if (i == retries-1) {
     *                     serverThread.close();
     *                     return null;
     *                 } else {
     *                     try {
     *                         serverThread.getJsonReader().skipValue();
     *                         System.out.println("skips");
     *                     } catch (IOException e) {
     *                         System.out.println("error");
     *                         System.out.println(e.getMessage());
     *                     }
     *                 }
     *             } catch (InterruptedException ie) {
     *                 serverThread.getMessageHandler().handleError(new InterruptedException("The thread got suddenly interrupted: " + ie.getMessage()));
     *             } catch (IOException e) {
     *                 if (e instanceof SocketTimeoutException) {
     *                     serverThread.getMessageHandler().handleError(new SocketTimeoutException("The connection timed out with " + serverThread.getRemoteAddress()));
     *                 } else {
     *                     serverThread.getMessageHandler().handleError(new IOException("An unknown error occurred", e));
     *                 }
     *                 serverThread.close();
     *                 return null;
     *             }
     *         }
     *         gson.
     *         return null;
     * @param serverThread
     * @return
     */
    public static Double matchVersion(ServerThread serverThread) {
        final Gson gson = new Gson();
        int attempts = 3;
        for(int i = 1; i <= attempts; i++) {
            try {
                while (serverThread.getJsonReader().hasNext()) {
                    try {
                        System.out.println("Has next");
                        JsonToken jt = serverThread.getJsonReader().peek();
                        if (JsonToken.BEGIN_OBJECT.equals(jt)) {
                            System.out.println("begin object detected");

                            ClientVersionMatcherPacket c = gson.fromJson(serverThread.getJsonReader(), ClientVersionMatcherPacket.class);
                            System.out.println(c);
                            //serverThread.getJsonReader().endObject();
                        } else {
                            serverThread.getJsonReader().skipValue();
                        }

                        Thread.sleep(100);
                    } catch (JsonSyntaxException | MalformedJsonException syntaxExcpetion) {
                        serverThread.getJsonReader().skipValue();
                        System.out.println(syntaxExcpetion.getMessage() + " 1");
                    }
                }

            }
             catch (JsonSyntaxException | MalformedJsonException syntaxE) {
                 System.out.println(syntaxE.getMessage() + " 2");
                 syntaxE.printStackTrace();


            } catch (IOException jioe) {
                System.out.println(jioe.getMessage());
            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        }

        return null;
    }

public static boolean isValidJson(String jsonToTest) {
        return isValidJson(jsonToTest, Object.class);
            }

    public  static <T> boolean isValidJson(String jsonToTest, Class<T> classType) {
        try {
            new Gson().fromJson(jsonToTest, classType);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        ClientVersionMatcherPacket vmp = new ClientVersionMatcherPacket(1,new double[]{1,2,3});

        String json = gson.toJson(vmp);
        System.out.println(json);
        ClientVersionMatcherPacket newVMP = gson.fromJson(json, ClientVersionMatcherPacket.class);
        System.out.println(newVMP);
    }
}
