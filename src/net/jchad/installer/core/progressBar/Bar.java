package net.jchad.installer.core.progressBar;

public class Bar {

    private int maxValue = 100;

    private String progressBarSymbol = "=";

    private int charWidth = 10;

    String fullBar = "";

    private String suffix = "[";

    private int currentValue = 0;

    private String prefix = "]";
    private int repeats = 1;

    private int valueMultiplier = 1;

    public Bar(int maxValue, String progressBarSymbol, int charWidth, String suffix, String prefix, int repeats ) {
        this(maxValue);
        setProgressBarSymbol(progressBarSymbol);
        setCharWidth(charWidth);
        setSuffix(suffix);
        setPrefix(prefix);
        setRepeats(repeats);
    }

    public Bar(int maxValue) {
        setMaxValue(maxValue);
    }

    public void reset() {
        currentValue = 0;
    }

    public void update() {
        ++currentValue;
    }

    public void update(int multiplier ) {
        currentValue = valueMultiplier * multiplier;
    }

    Bar updateBar() {

        return this;
    }


    public Bar setMaxValue(int maxValue) {
        if (maxValue > 0) this.maxValue = maxValue;
        return this;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public String getProgressBarSymbol() {
        return progressBarSymbol;
    }

    public Bar setProgressBarSymbol(String progressBarSymbol) {
        this.progressBarSymbol = progressBarSymbol;
        return this;
    }

    public int getCharWidth() {
        return charWidth;
    }

    public Bar setCharWidth(int charWidth) {
        if (charWidth > 0) this.charWidth = charWidth;
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public Bar setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public Bar setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public int getRepeats() {
        return repeats;
    }

    public Bar setRepeats(int repeats) {
        if (repeats >= 1) this.repeats = repeats;
        return this;
    }

    public int getValueMultiplier() {
        return valueMultiplier;
    }

    public Bar setValueMultiplier(int valueMultiplier) {
        if (valueMultiplier >= 1) this.valueMultiplier = valueMultiplier;
        return this;
    }
}
