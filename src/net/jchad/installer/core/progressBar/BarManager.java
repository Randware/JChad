package net.jchad.installer.core.progressBar;

import java.util.*;

public class BarManager {

    private static final HashMap<String,BarManager> barManagers = new HashMap<>();

    public String name = "";
    public BarOwner barOwner;
    public Bar bar;

    public LinkedHashSet<BarDisplay> barDisplays = new LinkedHashSet<>();

    private BarManager(String name, BarOwner bo, LinkedHashSet<BarDisplay> displays) {
        setName(name);
        setBarOwner(bo);
        setDisplays(displays);
        barManagers.put(this.name, this);
    }

    static boolean register(String name, BarOwner bo, LinkedHashSet<BarDisplay> displays) {
        try {
            new BarManager(name, bo, displays);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    static boolean addDisplay(String name, BarDisplay display) {
        if (name == null || display == null) {return false;}
        if(barManagers.containsKey(name)) {
            barManagers.get(name).barDisplays.add(display);
            return true;
        } else {
            return false;
        }

    }


    private void setName(String name) {
        if (name != null && barManagers.containsKey(name)) {
            this.name = name;

        } else {
            throw new IllegalArgumentException(name + " has already been created or is null");

        }
    }

    private void setBarOwner(BarOwner barOwner) {
        if (barOwner != null) {
            this.barOwner = barOwner;
        } else {
            throw new IllegalArgumentException("barOwner can't be null");
        }
    }

    private void setBar(Bar bar) {

        if (bar != null) {
            this.bar = bar;
        } else {
            throw new IllegalArgumentException("bar can't be null");
        }
    }

    

    private void setDisplays(LinkedHashSet<BarDisplay> displays) {
        if (displays != null) {
            this.barDisplays = displays;
        } else {
            throw new IllegalArgumentException("displays can't be null!");
        }
    }

    public String getName() {
        return name;
    }
}
