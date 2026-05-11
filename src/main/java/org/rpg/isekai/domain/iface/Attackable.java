package org.rpg.isekai.domain.iface;

import org.rpg.isekai.domain.skill.Skill;

public interface Attackable<T extends Skill> {
    int getDamage(T skill);
    void attack(T skill, Damageable target);
}
