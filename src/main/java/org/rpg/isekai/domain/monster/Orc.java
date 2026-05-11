package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.skill.monster.HeavyBash;
import org.rpg.isekai.domain.skill.monster.SoulDrain;

import java.util.ArrayList;
import java.util.List;

public class Orc extends Monster {

    public Orc() {
        super("오크", 10, 200, MonsterType.NORMAL, new ArrayList<>(List.of(new HeavyBash(), new SoulDrain())));
    }

    @Override
    public int getDamage(Skill skill) {
        return 25;
    }
}
