package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.amorItem.Circlet;
import org.rpg.isekai.domain.item.amorItem.Gauntlet;
import org.rpg.isekai.domain.item.amorItem.Greaves;
import org.rpg.isekai.domain.skill.monster.BoneFragments;
import org.rpg.isekai.domain.skill.monster.HeavyBash;

import java.util.ArrayList;
import java.util.List;

public class MegaSkeleton extends Monster{
    public MegaSkeleton() {
        super("메가 스켈레톤", 10, MonsterType.BOSS, new Stat(8, 0, 3, 200, 0),
                new ArrayList<>(List.of(new HeavyBash(), new BoneFragments())),
                new Reward(200, List.of(
                        new Greaves(),
                        new Gauntlet(),
                        new Circlet()
                )), 20);
    }
}
