package net.jchad.installer.core.progressBar;

import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.interfaces.BarFunctional;

public enum BarStatus {
    PROGRESS_START((bar, display) -> display.updateOnStart(bar)),
    PROGRESS_UPDATE((bar, display) -> display.updateOnUpdate(bar)),
    PROGRESS_END((bar, display) -> display.updateOnEnd(bar)),

    PROGRESS_FAILED((bar, display) -> display.updateOnFailed(bar));

    public final BarFunctional barFunctional;

    private BarStatus(BarFunctional bf) {
            barFunctional = bf;
    }



    @Override
    public String toString() {
        return   name().charAt(0) + name().toLowerCase().substring(1).replace("_", " ");
    }


}
