package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.monster.SlimeBash;
import org.rpg.isekai.domain.skill.monster.SlimeSticky;

import java.util.ArrayList;
import java.util.List;

public class GiantSlime extends Monster{
    public GiantSlime() {
        super("자이언트 슬라임", 1, MonsterType.BOSS, new Stat(10, 2, 60, 10),
                new ArrayList<>(List.of(new SlimeBash(), new SlimeSticky())),
                new Reward(10, List.of()), 50);
    }
}
