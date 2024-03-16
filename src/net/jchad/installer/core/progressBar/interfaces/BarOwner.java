package net.jchad.installer.core.progressBar.interfaces;

import net.jchad.installer.core.progressBar.Bar;
import net.jchad.installer.core.progressBar.BarDisplay;

import java.util.LinkedHashSet;

/**
 * This class has no use case.
 * It will be removed if there is no point in using it
 */
@Deprecated
interface BarOwner {

    boolean register(String name, Bar bar, LinkedHashSet<BarDisplay> displays);

    boolean register(String name,  LinkedHashSet<BarDisplay> displays);

    void setBar(Bar bar);

    void updateDisplays();

    void updateDisplays(Bar bar);

    Bar getBar();
}
