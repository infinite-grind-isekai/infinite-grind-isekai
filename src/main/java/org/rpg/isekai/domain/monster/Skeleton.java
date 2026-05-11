package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.skill.monster.HeavyBash;
import org.rpg.isekai.domain.skill.monster.BoneFragments;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends Monster {

    public Skeleton() {
        super("스켈레톤", 1, 30, MonsterType.NORMAL, new ArrayList<>(List.of(new HeavyBash(), new BoneFragments())));
    }

    @Override
    public int getDamage(Skill skill) {
        return 5;
    }
}
