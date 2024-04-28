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
     * @param serverThread
     * @return
     */
    public static Double matchVersion(ServerThread serverThread) {
        final Gson gson = new Gson();
        long intervals = serverThread.getConnectionRefreshIntervalMillis();
        int attempts = serverThread.getRetriesOnMalformedJSONduringVersioning();
        for(int i = 1; i <= attempts; i++) {
            try {
                while (serverThread.getJsonReader().hasNext()) {

                        System.out.println("Has next");
                        JsonToken jt = serverThread.getJsonReader().peek();
                        if (JsonToken.BEGIN_OBJECT.equals(jt)) {
                            System.out.println("begin object detected");

                            ClientVersionMatcherPacket c = gson.fromJson(serverThread.getJsonReader(), ClientVersionMatcherPacket.class);
                            System.out.println(c);
                            //serverThread.getJsonReader().endObject();
                        } else {
                           serverThread.getJsonReader().skipValue(); //<-- idk why but this is a factory of problems
                            System.out.println("Except");
                            throw new MalformedJsonException("The given data is not representable as JSON");
                        }

                        Thread.sleep(intervals);

                }

            }
             catch (JsonSyntaxException | MalformedJsonException ije) {
                 if (i >= attempts) {
                    serverThread.getMessageHandler().handleError(new MalformedJsonException("The received data from " + serverThread.getRemoteAddress()
                            + " could not be parsed to JSON, during the versioning process. The connection will be terminated now!", ije));
                    serverThread.close("Data could not be parsed into JSON, during the versioning process. ");
                 } else {
                     serverThread.getMessageHandler().handleWarning("The received data from %s could not be parsed to JSON, during the versioning process. The connection gets terminated after %d more failed attempt(s)"
                             .formatted(serverThread.getRemoteAddress(), attempts - i));
                 }


            } catch (IOException io) {
                serverThread.getMessageHandler().handleError(new IOException("An IOException occurred unexpectedly, during the versioning process. (Connection closed)", io));
                serverThread.close("IOException while versioning");
            } catch (InterruptedException ie) {
                serverThread.getMessageHandler().handleError(new Exception("The current connection thread got interrupted unexpectedly, during the versioning process during the versioning process. (Connection closed)" , ie));
                serverThread.close("Thread got interrupted while versioning");
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
