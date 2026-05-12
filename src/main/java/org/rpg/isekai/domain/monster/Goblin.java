package org.rpg.isekai.domain.monster;

import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.monster.GoblinPunch;
import org.rpg.isekai.domain.skill.monster.PoisonSpit;

import java.util.ArrayList;
import java.util.List;

public class Goblin extends Monster {

    public Goblin() {
        super("고블린", 5, MonsterType.NORMAL, new Stat(12, 0, 5, 80, 20),
                new ArrayList<>(List.of(new GoblinPunch(), new PoisonSpit())),
                new Reward(30, List.of()), 50);
    }
}
