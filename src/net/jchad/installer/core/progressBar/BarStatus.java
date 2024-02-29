package net.jchad.installer.core.progressBar;

import net.jchad.installer.core.progressBar.Bar;

public enum BarStatus {
    PROGRESS_START,
    PROGRESS_UPDATE,
    PROGRESS_END,

    PROGRESS_FAILED;

    @Override
    public String toString() {
        return   name().charAt(0) + name().toLowerCase().substring(1).replace("_", " ");
    }


}
