package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.PotionItem.HealthPotion;
import org.rpg.isekai.domain.item.MaterialItem.GoblinEar;
import org.rpg.isekai.domain.skill.monster.PoisonSpit;

import java.util.ArrayList;
import java.util.List;

public class Harpy extends Monster {
    public Harpy() {
        super("하피", 15, MonsterType.NORMAL, new Stat(35, 15, 300, 100),
                new ArrayList<>(List.of(new PoisonSpit())),
                new Reward(100, List.of(new HealthPotion(), new GoblinEar())), 150);
    }
}
