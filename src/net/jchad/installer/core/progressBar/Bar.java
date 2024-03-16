package net.jchad.installer.core.progressBar;

public final class Bar {

    private long maxProgress;
    private long currentProgress;

    private long lastAddedProgress;

    private BarStatus barStatus;

    private BarUpdater barUpdater;

    private boolean initSetup = true;


    public Bar(long maxProgress) {
        this(maxProgress, null);
    }

    public Bar(long maxProgress, BarUpdater barUpdater) {
        this(maxProgress, barUpdater, 0);
    }

    public Bar(long maxProgress, BarUpdater barUpdater, long currentProgress) {
        this(maxProgress, barUpdater, currentProgress, 0,BarStatus.PROGRESS_START);
    }

    /**
     * This class represents a progression Bar.
     *
     * @param maxProgress The maximum progress the bar can reach
     * @param currentProgress The current progress of the bar
     * @param lastAddedProgress The value of the last added progress
     * @param barStatus The bar status (Maybe gets deleted soon)
     */
    public Bar(long maxProgress,BarUpdater barUpdater, long currentProgress, long lastAddedProgress, BarStatus barStatus) {
        setMaxProgress(maxProgress);
        setCurrentProgress(currentProgress);
        setLastAddedProgress(lastAddedProgress);
        setBarStatus(barStatus);
        setBarUpdater(barUpdater);
        initSetup = false;
    }

    private Bar(Bar bar) {
        this(bar.maxProgress , bar.barUpdater , bar.currentProgress, bar.lastAddedProgress, bar.barStatus);
    }


    public long getMaxProgress() {
        return maxProgress;
    }

    public Bar setMaxProgress(long maxProgress) {
        if (maxProgress < 0) throw new IllegalArgumentException("maxProgress can't be <0");
        this.maxProgress = maxProgress;
        callUpdater();
        return this;
    }

    public long getCurrentProgress() {

        return currentProgress;
    }

    public Bar setCurrentProgress(long currentProgress) {
        if (currentProgress < 0) throw new IllegalArgumentException("currentProgress can't be >0");
        this.currentProgress = currentProgress;
        callUpdater();
        return this;
    }

    public long getLastAddedProgress() {

        return lastAddedProgress;
    }

    public Bar setLastAddedProgress(long lastAddedProgress) {
        if (lastAddedProgress < 0) throw new IllegalArgumentException("lastProgress can't be >0");
        this.lastAddedProgress = lastAddedProgress;
        callUpdater();
        return this;
    }

    public boolean isFinished() {
        return maxProgress <= currentProgress;
    }

    public void setBarStatus(BarStatus barStatus) {
        this.barStatus = barStatus;
        callUpdater();
    }

    public BarStatus getBarStatus() {
        return barStatus;
    }

    public Bar addProgress(long progress) {
        if (progress < 0) throw new IllegalArgumentException("The progress to add can't be <0");
        if (currentProgress + progress <= maxProgress) {
            lastAddedProgress = progress;
            currentProgress += progress;
        } else {
            lastAddedProgress = maxProgress - currentProgress;
            currentProgress = maxProgress;
        }

        callUpdater();
        return this;
    }

    /**
     * Resets the current progress and the last added Progress
     * @return
     */
    public Bar reset() {
        currentProgress = 0;
        lastAddedProgress=0;
        callUpdater();
        return this;
    }

    public void setBarUpdater(BarUpdater barUpdater) {
        this.barUpdater = barUpdater;
    }

    @Override
    protected Object clone() {
        return new Bar(this);
    }


    private void callUpdater() {
        if (initSetup || barUpdater == null) return; //Prevents any updates when the constructor calls the setters or prevents update if barUpdater is not set

        barUpdater.updateDisplays(this);
    }

}