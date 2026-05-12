package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.AmorItem.TitanArmor;
import org.rpg.isekai.domain.item.MaterialItem.SkeletonBone;
import org.rpg.isekai.domain.item.PotionItem.HealthPotion;
import org.rpg.isekai.domain.skill.monster.BoneFragments;
import org.rpg.isekai.domain.skill.monster.HeavyBash;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends Monster {

    public Skeleton() {
        super("스켈레톤", 1, MonsterType.NORMAL, new Stat(8, 3, 30, 0),
                new ArrayList<>(List.of(new HeavyBash(), new BoneFragments())),
                new Reward(20, List.of(
                        new TitanArmor(),
                        new HealthPotion(),
                        new SkeletonBone()
                )), 20);
    }
}
