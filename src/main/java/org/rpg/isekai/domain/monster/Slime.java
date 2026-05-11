package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.skill.monster.SlimeBash;
import org.rpg.isekai.domain.skill.monster.SlimeSticky;

import java.util.ArrayList;
import java.util.List;

public class Slime extends Monster {

    public Slime() {
        super("슬라임", 1, 50, MonsterType.NORMAL, new ArrayList<>(List.of(new SlimeBash(), new SlimeSticky())));
    }

    @Override
    public int getDamage(Skill skill) {
        return 5;
    }
}
