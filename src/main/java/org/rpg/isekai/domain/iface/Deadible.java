package org.rpg.isekai.domain.iface;

public interface Deadible {
    int getHealth();
    default boolean isDead() {
        return getHealth() <= 0;
    };
}
