package org.rpg.isekai.domain.monster;

import lombok.Getter;
import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.Deadible;
import org.rpg.isekai.domain.iface.HasLevel;

@Getter
public abstract class Monster implements HasLevel, Attackable, Damageable {

    private String name;
    private int level;
    private MonsterType type;

}
