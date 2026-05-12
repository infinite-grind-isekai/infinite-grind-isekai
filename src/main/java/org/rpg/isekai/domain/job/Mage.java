package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.character.FireBall;
import org.rpg.isekai.domain.skill.character.IceSpear;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Mage extends Job {

    public Mage() {
        this.name = JobKind.MAGE.getName();
        this.stat = new Stat(20, 5.0, 0, 70, 120);
        this.jobKind = JobKind.MAGE;
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new FireBall(),
                new IceSpear()
        );
    }
}
