package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.skill.monster.FireBreath;
import org.rpg.isekai.domain.skill.monster.DragonTail;

import java.util.ArrayList;
import java.util.List;

public class AncientDragon extends Monster {

    public AncientDragon() {
        super("고대 드래곤", 100, 5000, MonsterType.BOSS, new ArrayList<>(List.of(new FireBreath(), new DragonTail())));
    }

    @Override
    public int getDamage(Skill skill) {
        return 150;
    }
}
