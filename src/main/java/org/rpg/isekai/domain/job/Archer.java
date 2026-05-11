package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.character.DoubleShot;
import org.rpg.isekai.domain.skill.character.PiercingArrow;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Archer extends Job {

    public Archer() {
        this.name = "궁수";
        this.stat = new Stat(35, 0, 90, 60);
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new DoubleShot(),
                new PiercingArrow()
        );
    }
}
