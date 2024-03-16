package net.jchad.installer.core.progressBar;

import java.util.*;

public class BarUpdater {

    private static final HashMap<String, BarUpdater> barUpdater = new HashMap<>();
    private static final HashMap<String, LinkedHashSet<BarDisplay>> displaysInQueue = new HashMap<>(); //If a display hooks before the BarUpdater get created, the display(s) will wait here till the BarUpdater gets created
    private String name = "";
    private Bar bar;

    private LinkedHashSet<BarDisplay> barDisplays = new LinkedHashSet<>();

    /**
     * This manages everything of the progress bar. like updates, new registers, ...
     *
     * @param name
     * @param bar
     * @param displays
     */
    protected BarUpdater(String name, Bar bar, LinkedHashSet<BarDisplay> displays) {
        setName(name);
        setBar(bar);
        setDisplays(displays);
        barUpdater.put(this.name, this);
        checkToAddQueue();
    }

    /**
     * Safely deletes the BarUpdater instance and removes ALL associated displays and adds them to the displaysInQueue
     * In the display queue the displays await a new BarUpdater with the name they are hooked to
     * @param bu the instance of the BarUpdater that gets unregistered
     */
    public static void unregister(BarUpdater bu) {
        if (barUpdater.containsKey(bu)) {
            barUpdater.remove(bu.getName());
            if (bu.barDisplays != null && !bu.barDisplays.isEmpty()) {
                displaysInQueue.put(bu.getName(), bu.barDisplays);
                bu.barDisplays.clear();
            }
        }
    }

    /**
     * Fully unregisters the BarUpdater instance and unhooks all displays associated with it
     * @param bu the instance of the BarUpdater that gets unregistered
     */
    public static void fullUnregister(BarUpdater bu) {
        if (barUpdater.containsKey(bu)) {
            bu.barDisplays.forEach((display) -> display.unhook(bu.name));
            unregister(bu);
        }
    }

    /**
     * Removes a display from the display set
     *
     * @param name The chanel where the display is hooked
     * @param barDisplay The barDisplay to get removed
     * @return If the display got successfully removed
     */
    public static boolean unhook(String name, BarDisplay barDisplay) {
        if (barUpdater.containsKey(name)) {
           return barUpdater.get(name).barDisplays.remove(barDisplay);
        }
        return false;
    }
/*

    protected final boolean register() {
        return register(name,new Bar(100, this), new LinkedHashSet<>());
    }


    */
/** This lets you register a "chanel". A chanel has one or multiple displays that display the Bar once it gets updated.
     * Every chanel has a name with whom the BarDisplay can "hook" itself on that chanel. Only BarDisplays get updated that belongs to a chanel
     *
     * @param name The name of the Chanel
     * @param displays The displays of the chanel that get updated
     * @return If the chanel got registered
     *//*

     protected final boolean register(String name,  LinkedHashSet<BarDisplay> displays) {
        return register(name,new Bar(100, this), displays);
    }

    */
/** This lets you register a "chanel". A chanel has one or multiple displays that display the Bar once it gets updated.
     * Every chanel has a name with whom the BarDisplay can "hook" itself on that chanel. Only BarDisplays get updated that belongs to a chanel
     *
     * @param name The name of the Chanel.
     * @param bar The ProgressBar (NOT VISUAL)
     * @param displays The displays of the chanel that get updated
     * @return If the chanel got registered
     *//*

    protected final boolean register(String name,  Bar bar, LinkedHashSet<BarDisplay> displays) {
        try {
            new BarUpdater(name,  bar, displays);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }
*/

    /**
     * Adds display(s) to a chanel
     * @param displays
     * @return
     */
    protected final boolean addDisplay( final BarDisplay... displays) {
        return addDisplay(getName(), displays);
    }

    /**
     * Adds displays to a chanel.
     * These displays get updated once the Bar changes
     * @param name The name of the chanel to which the displays shall get added
     * @param display The display(s) to add
     * @return True - if the displays got added to a chanel; False - added the displays to the queue. These displays get added if a BarUpdater gets created with the same name
     */
    protected static boolean addDisplay(String name, BarDisplay... display) {
        if (name == null || display == null) {return false;}
        if(barUpdater.containsKey(name)) {
            barUpdater.get(name).barDisplays.addAll(List.of(display));
            return true;
        } else {
            displaysInQueue.put(name,new LinkedHashSet<>(List.of(display)));
            return false;
        }

    }

    /**
     * Sets the name for the chanel
     * @param name
     */
    private void setName(String name) {

        if (name != null && !barUpdater.containsKey(name)) {
            this.name = name;

        } else {
            throw new IllegalArgumentException(name + " has already been created or is null");

        }
    }

    /**
     * Sets a COPY of the given bar!
     * @param bar
     */
    protected final void setBar(Bar bar) {
        if (bar != null) {
            bar = (Bar) bar.clone();
            this.bar = bar;
            updateDisplays(bar);
        } else {
            this.bar = null;
        }
    }

    /**
     * Use this methode to update the BarDisplays in the current chanel.
     * Uses the current set bar to update
     */
    protected final void updateDisplays() {
        if (bar != null) updateDisplays(getBar());
    }

    /**
     * Adds queued displays if the name of this instance is in the HashMap of the queued displays
     */
    private void checkToAddQueue() {
        if (displaysInQueue.containsKey(name)) {
            addDisplay(displaysInQueue.get(name).toArray(new BarDisplay[0]));
        }
    }

    /**
     *  Use this methode to update the BarDisplays in the chanel.
     * @param bar Uses this bar to update
     *
     */
    protected final void updateDisplays(Bar bar) {
        barDisplays.forEach(display -> {
            display.update(bar);
            bar.getBarStatus().barFunctional.apply(bar,display);
        });

    }

    protected Bar getBar() {
        return bar;
    }

    /**
     * Sets the display(s) for the chanel
     * @param displays The display that get added
     */
    private void setDisplays(LinkedHashSet<BarDisplay> displays) {
        if (displays != null) {
            this.barDisplays = displays;
        } else {
            throw new IllegalArgumentException("displays can't be null!");
        }
    }

    protected final String getName() {
        return name;
    }

    public LinkedHashSet<BarDisplay> getBarDisplays() {
        return barDisplays;
    }
}
