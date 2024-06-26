package net.jchad.shared.cryptography.crypterUnits;

/**
 * These are different units to avoid exception
 */
public enum KeyUnit {
    LENGTH_128(128),
    LENGTH_192(192),
    LENGTH_256(256),
    DEFAULT(LENGTH_256.value);
    public final int value;
    private KeyUnit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
