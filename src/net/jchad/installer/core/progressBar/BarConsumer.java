package net.jchad.installer.core.progressBar;

public interface BarConsumer {

    public void updated(Bar bar);

    default void openHandle() {

    }

}
