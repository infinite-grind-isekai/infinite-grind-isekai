package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.FireBall;
import org.rpg.isekai.domain.skill.IceSpear;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Mage extends Job {

    public Mage() {
        this.name = "마법사";
        this.stat = new Stat(20, 0, 70, 120);
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new FireBall(),
                new IceSpear()
        );
    }
}
