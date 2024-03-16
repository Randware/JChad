package net.jchad.installer.core.progressBar;

import java.util.LinkedHashSet;

public abstract class BarUpdater extends BarManager implements BarOwner {
    public BarUpdater(String name,Bar bar, LinkedHashSet<BarDisplay> displays) {
        super(name,bar,displays);
    }

    @Override
    public boolean register(String name,  Bar bar, LinkedHashSet<BarDisplay> displays) {
        return super.register(name,bar,displays);
    }

    @Override
    public boolean register(String name,  LinkedHashSet<BarDisplay> displays) {
       return super.register(name,  displays);
    }

    @Override
    public void setBar(Bar bar) {
        super.setBar(bar);
    }

    @Override
    public Bar getBar() {
        return super.getBar();
    }
}
