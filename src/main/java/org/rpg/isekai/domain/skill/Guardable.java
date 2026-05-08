package org.rpg.isekai.domain.skill;

public sealed interface Guardable permits Skill {
    boolean isGuarded();
}
