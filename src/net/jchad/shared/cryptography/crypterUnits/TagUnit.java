package net.jchad.shared.cryptography.crypterUnits;
/**
 * These are different units to avoid exception
 */
public enum TagUnit {
    LENGTH_96(96),
    LENGTH_104(104),
    LENGTH_112(112),
    LENGTH_120(120),
    LENGTH_128(128),
    DEFAULT(LENGTH_128.getValue());

    public final int value;

    private TagUnit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
