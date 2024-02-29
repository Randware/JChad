package net.jchad.installer.core.progressBar;

public interface BarDisplay {

    public  void update(Bar bar, BarStatus status);

    default boolean hook(String name) {
        return BarManager.addDisplay(name, this);
    }

}
