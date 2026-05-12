package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.item.amorItem.DragonScaleMail;
import org.rpg.isekai.domain.item.materialItem.DragonScale;
import org.rpg.isekai.domain.item.potionItem.HeartOfDragon;
import org.rpg.isekai.domain.item.weaponItem.ThunderstrikeGun;
import org.rpg.isekai.domain.skill.monster.DragonTail;
import org.rpg.isekai.domain.skill.monster.FireBreath;

import java.util.ArrayList;
import java.util.List;

public class AncientDragon extends Monster {

    public AncientDragon() {
        super("고대 드래곤", 100, MonsterType.BOSS, new Stat(150, 0, 80, 5000, 1000),
                new ArrayList<>(List.of(new FireBreath(), new DragonTail())),
                new Reward(500, List.of(
                        new HeartOfDragon(),
                        new ThunderstrikeGun(),
                        new DragonScaleMail(),
                        new DragonScale()
                )), 1000);
    }
}
