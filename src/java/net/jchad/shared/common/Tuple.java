package net.jchad.shared.common;

import java.util.Objects;

public class Tuple<first, second> {
    public first one;
    public second two;

    public Tuple(first one, second two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) object;
        return Objects.equals(one, tuple.one) && Objects.equals(two, tuple.two);
    }

    @Override
    public String toString() {
        return "["+ one + "," + two + "]";
    }

    public static <first, second> Tuple<first, second> of(first one, second two) {
        return new Tuple<>(one, two);
    }

}
