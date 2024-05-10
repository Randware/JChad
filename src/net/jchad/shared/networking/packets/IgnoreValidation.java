package net.jchad.shared.networking.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotates if a variable should skip {@link Packet#isValid() packet validation}.
 * So it basically lets a variable be null without invalidating the entire packet.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreValidation {

}
