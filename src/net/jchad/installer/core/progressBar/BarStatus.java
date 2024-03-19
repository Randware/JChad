package net.jchad.installer.core.progressBar;

import net.jchad.installer.core.progressBar.interfaces.BarFunctional;

import java.util.Optional;

public enum  BarStatus{
    PROGRESS_START((bar, display) -> display.updateOnStart(bar)),
    PROGRESS_UPDATE((bar, display) -> display.updateOnUpdate(bar)),
    PROGRESS_END((bar, display) -> display.updateOnEnd(bar)),

    PROGRESS_FAILED((bar, display) -> display.updateOnFailed(bar));

    private Exception exception;


    public final BarFunctional barFunctional;

    private BarStatus(BarFunctional bf) {
            barFunctional = bf;
    }

    /**
     * Get the associated enum
     *
     * @return
     */
    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    /**
     * associate an Exception with the enum type.
     * Mostly used for the status Failed;
     * @param exception The exception that gets associated with the enum
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return   name().charAt(0) + name().toLowerCase().substring(1).replace("_", " ");
    }


}
