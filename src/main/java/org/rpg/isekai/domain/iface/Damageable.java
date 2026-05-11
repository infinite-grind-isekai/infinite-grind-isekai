package org.rpg.isekai.domain.iface;

import org.rpg.isekai.domain.skill.Skill;

public interface Damageable extends Deadible{
    void damage(Skill skill, int damage);
}
