package net.jchad.server.model.cryptography.tagUnit;

public enum Tag {
    LENGTH_96(96),
    LENGTH_104(104),
    LENGTH_112(112),
    LENGTH_120(120),
    LENGTH_128(128),
    DEFAULT(LENGTH_128.getValue());

    private final int val;

    private Tag(int val) {
        this.val = val;
    }

    public int getValue() {
        return val;
    }

}
