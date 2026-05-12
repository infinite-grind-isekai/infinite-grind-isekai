package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.AmorItem.IronPlate;
import org.rpg.isekai.domain.item.MaterialItem.OrcTooth;
import org.rpg.isekai.domain.item.PotionItem.ManaPotion;
import org.rpg.isekai.domain.item.PotionItem.PhoenixFeather;
import org.rpg.isekai.domain.item.WeaponItem.ShadowReaper;
import org.rpg.isekai.domain.skill.monster.HeavyBash;
import org.rpg.isekai.domain.skill.monster.SoulDrain;

import java.util.ArrayList;
import java.util.List;

public class Orc extends Monster {

    public Orc() {
        super("오크", 10, MonsterType.NORMAL, new Stat(25, 0, 10, 200, 50),
                new ArrayList<>(List.of(new HeavyBash(), new SoulDrain())),
                new Reward(80, List.of(
                        new PhoenixFeather(),
                        new ShadowReaper(),
                        new ManaPotion(),
                        new IronPlate(),
                        new OrcTooth()
                )), 100);
    }
}
