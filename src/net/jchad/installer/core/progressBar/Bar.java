package net.jchad.installer.core.progressBar;

public class Bar {
    private long maxProgress;
    private long currentProgress;

    private long lastAddedProgress;

    private BarStatus barStatus;

    public Bar() {this(100);}

    public Bar(long maxProgress) {
        this(maxProgress, 0);
    }

    public Bar(long maxProgress, long currentProgress) {
        this(maxProgress, currentProgress, 0,BarStatus.PROGRESS_START);
    }

    public Bar(long maxProgress, long currentProgress, long lastAddedProgress, BarStatus barStatus) {
        setMaxProgress(maxProgress);
        setCurrentProgress(currentProgress);
        setLastAddedProgress(lastAddedProgress);
        setBarStatus(barStatus);
    }

    private Bar(Bar bar) {
        this(bar.maxProgress, bar.currentProgress, bar.lastAddedProgress, bar.barStatus);
    }


    public long getMaxProgress() {
        return maxProgress;
    }

    public Bar setMaxProgress(long maxProgress) {
        if (maxProgress < 0) throw new IllegalArgumentException("maxProgress can't be <0");
        this.maxProgress = maxProgress;
        return this;
    }

    public long getCurrentProgress() {

        return currentProgress;
    }

    public Bar setCurrentProgress(long currentProgress) {
        if (currentProgress < 0) throw new IllegalArgumentException("currentProgress can't be >0");
        this.currentProgress = currentProgress;
        return this;
    }

    public long getLastAddedProgress() {
        return lastAddedProgress;
    }

    public Bar setLastAddedProgress(long lastAddedProgress) {
        if (lastAddedProgress < 0) throw new IllegalArgumentException("lastProgress can't be >0");
        this.lastAddedProgress = lastAddedProgress;
        return this;
    }

    public boolean isFinished() {
        if (maxProgress <= currentProgress) return true;
        else return false;
    }

    public void setBarStatus(BarStatus barStatus) {
        this.barStatus = barStatus;
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

        return this;
    }

    /**
     * Resets the current progress and the last added Progress
     * @return
     */
    public Bar reset() {
        currentProgress = 0;
        lastAddedProgress=0;
        return this;
    }

    @Override
    protected Object clone() {
        return new Bar(this);
    }


}