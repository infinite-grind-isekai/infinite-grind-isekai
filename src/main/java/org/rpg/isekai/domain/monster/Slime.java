package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.AmorItem.LeatherArmor;
import org.rpg.isekai.domain.item.MaterialItem.SlimeJelly;
import org.rpg.isekai.domain.item.PotionItem.EnergyDrink;
import org.rpg.isekai.domain.skill.monster.SlimeBash;
import org.rpg.isekai.domain.skill.monster.SlimeSticky;

import java.util.ArrayList;
import java.util.List;

public class Slime extends Monster {

    public Slime() {
        super("슬라임", 1, MonsterType.NORMAL, new Stat(5, 2, 50, 10),
                new ArrayList<>(List.of(new SlimeBash(), new SlimeSticky())),
                new Reward(10, List.of(new EnergyDrink(), new LeatherArmor(), new SlimeJelly())));
    }
}
