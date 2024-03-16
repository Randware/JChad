package net.jchad.installer.core.progressBar;

import java.util.LinkedHashSet;

interface BarOwner {

    boolean register(String name, Bar bar, LinkedHashSet<BarDisplay> displays);

    boolean register(String name,  LinkedHashSet<BarDisplay> displays);

    void setBar(Bar bar);

    Bar getBar();
}
