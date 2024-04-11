package net.jchad.server.model.server.implementations.v1;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.jchad.server.model.server.implementations.v1.packets.PacketTypes;
import net.jchad.server.model.server.implementations.v1.packets.SubType;

public class Packet {

    private final int VERSION = 1;

    private  PacketTypes type;
    private  SubType subType;

    public static void main(String[] args) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
    }
}
