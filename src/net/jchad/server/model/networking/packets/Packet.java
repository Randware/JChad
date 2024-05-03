package net.jchad.server.model.networking.packets;

import com.google.gson.Gson;

public interface Packet<T extends Packet<?>> {
    Gson gson = new Gson();
    default String toJSON() {
        return gson.toJson(this);
    }

     default T fromJSON(String jsonObject) {
        return (T) gson.fromJson(jsonObject, this.getClass());
    }
}
