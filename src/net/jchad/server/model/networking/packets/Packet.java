package net.jchad.server.model.networking.packets;

import com.google.gson.Gson;

public interface Packet{
    Gson gson = new Gson();
    default String toJSON() {
        return gson.toJson(this);
    }

    static <T extends Packet> T fromJSON(String jsonObject, Class<T> classOfPacket) {
        return gson.fromJson(jsonObject, classOfPacket);
    }
}
