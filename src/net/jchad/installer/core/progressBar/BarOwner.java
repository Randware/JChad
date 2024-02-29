package net.jchad.installer.core.progressBar;

import java.util.LinkedHashSet;

public interface BarOwner {

    default boolean register(String name) {
        return BarManager.register(name, this, new LinkedHashSet<>());
    }

}
