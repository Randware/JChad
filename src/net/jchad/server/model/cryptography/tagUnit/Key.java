package net.jchad.server.model.cryptography.tagUnit;

public enum Key {
    LENGTH_128(128),
    LENGTH_192(192),
    LENGTH_256(256),
    DEFAULT(LENGTH_256.value);
    public final int value;
    private Key(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
