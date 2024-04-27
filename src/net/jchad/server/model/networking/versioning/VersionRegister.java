package net.jchad.server.model.networking.versioning;

import net.jchad.server.model.error.MessageHandler;
import net.jchad.server.model.server.ServerThread;

import java.util.HashMap;
import java.util.function.Supplier;

public abstract class VersionRegister {

    private final static HashMap<Double, Supplier<? extends VersionRegister>> versions = new HashMap<>();
    final ServerThread serverThread;

    public VersionRegister(MessageHandler messageHandler, ServerThread serverThread) throws InstantiationException {
        if (serverThread == null) throw new InstantiationException("The Server-Socket can not be null");
        this.serverThread = serverThread;
    }

    abstract public void incomingPacket(String packet);

    abstract public void close(String reason);


    public static boolean registerVersion(Double versionNumber, Supplier<? extends VersionRegister> supplier) {
        if (versionNumber != null && !versions.containsKey(versionNumber) && supplier != null) {
            versions.put(versionNumber, supplier);
            return true;
        } else {
            return false;
        }
    }

    public static boolean unregisterVersion(Double versionNumber) {
        if (versionNumber != null && versions.containsKey(versionNumber)) {
            versions.remove(versionNumber);
        }
        return false;
    }
}
