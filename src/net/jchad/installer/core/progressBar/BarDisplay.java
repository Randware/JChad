package net.jchad.installer.core.progressBar;

import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.BarUpdater;

public interface BarDisplay {

    /**
     * This methode gets called everytime when the progress bar gets updated
     * @param bar
     */
    void update(Bar bar);

    /**
     * Calls this if the BarStatus is failed
     * @param bar
     */
    void updateOnFailed(Bar bar);
    /**
     * Calls this if the BarStatus is start
     * @param bar
     */
    void updateOnStart(Bar bar);
    /**
     * Calls this if the BarStatus is end
     * @param bar
     */
    void updateOnEnd(Bar bar);

    /**
     * Calls this if the BarStatus is update
     * @param bar
     */
    void updateOnUpdate(Bar bar);

    /**
     * This lets you configures which BarUpdater should trigger an update (event)
     * @param name The name of the chanel
     * @return  true: if the display got add to the chanel; false: If the display got added to queue (the queue adds itself if a BarUpdater with the same name gets created)
     */
    default boolean hook(String name) {
        return BarUpdater.addDisplay(name, this);
    }

    default  boolean unhook(String name){return BarUpdater.unhook(name, this);}

}
