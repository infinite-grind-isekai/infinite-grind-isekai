package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.AmorItem.IronPlate;
import org.rpg.isekai.domain.item.MaterialItem.SkeletonBone;
import org.rpg.isekai.domain.skill.monster.HeavyBash;

import java.util.ArrayList;
import java.util.List;

public class Golem extends Monster {
    public Golem() {
        super("골렘", 30, MonsterType.NORMAL, new Stat(60, 100, 1500, 0),
                new ArrayList<>(List.of(new HeavyBash())),
                new Reward(200, List.of(new IronPlate(), new SkeletonBone())), 500);
    }
}
