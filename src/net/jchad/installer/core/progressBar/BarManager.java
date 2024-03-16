package net.jchad.installer.core.progressBar;

import java.util.*;

public class BarManager {

    private static final HashMap<String,BarManager> barManagers = new HashMap<>();

    private String name = "";
    private Bar bar;

    public LinkedHashSet<BarDisplay> barDisplays = new LinkedHashSet<>();

    BarManager(String name,Bar bar, LinkedHashSet<BarDisplay> displays) {
        setName(name);
        setBar(bar);
        setDisplays(displays);
        barManagers.put(this.name, this);
    }

     boolean register(String name,  LinkedHashSet<BarDisplay> displays) {
        return register(name,new Bar(), displays);
    }

    boolean register(String name,  Bar bar, LinkedHashSet<BarDisplay> displays) {
        try {
            new BarManager(name,  bar, displays);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    boolean addDisplay(BarDisplay... displays) {
        return addDisplay(getName(), displays);
    }

    static boolean addDisplay(String name, BarDisplay... display) {
        if (name == null || display == null) {return false;}
        if(barManagers.containsKey(name)) {
            barManagers.get(name).barDisplays.addAll(List.of(display));
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


    void setBar(Bar bar) {
        bar = (Bar) bar.clone();
        if (bar != null) {
            this.bar = bar;
            updateDisplays(bar);
        } else {
            throw new IllegalArgumentException("bar can't be null");
        }
    }


    void updateDisplays() {
        updateDisplays(getBar());
    }
    void updateDisplays(Bar bar) {
        barDisplays.forEach(display -> display.update(bar));
    }

    Bar getBar() {
        return bar;
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
