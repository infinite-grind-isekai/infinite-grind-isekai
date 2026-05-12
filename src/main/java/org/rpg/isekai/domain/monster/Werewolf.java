package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.WeaponItem.IronSword;
import org.rpg.isekai.domain.item.MaterialItem.OrcTooth;
import org.rpg.isekai.domain.skill.monster.HeavyBash;

import java.util.ArrayList;
import java.util.List;

public class Werewolf extends Monster {
    public Werewolf() {
        super("워우울프", 50, MonsterType.NORMAL, new Stat(100, 40, 2500, 200),
                new ArrayList<>(List.of(new HeavyBash())),
                new Reward(300, List.of(new IronSword(), new OrcTooth())), 800);
    }
}
