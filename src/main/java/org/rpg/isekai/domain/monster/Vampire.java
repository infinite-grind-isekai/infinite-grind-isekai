package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.weaponItem.MagicStaff;
import org.rpg.isekai.domain.item.potionItem.ManaPotion;
import org.rpg.isekai.domain.skill.monster.SoulDrain;

import java.util.ArrayList;
import java.util.List;

public class Vampire extends Monster {
    public Vampire() {
        super("뱀파이어", 80, MonsterType.BOSS, new Stat(120, 0.0, 60, 4000, 1000),
                new ArrayList<>(List.of(new SoulDrain())),
                new Reward(1000, List.of(new MagicStaff(), new ManaPotion())), 2000);
    }
}
