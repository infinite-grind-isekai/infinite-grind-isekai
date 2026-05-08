package org.rpg.isekai.domain.system;

public sealed interface Endable permits Game {
    void end();
}
