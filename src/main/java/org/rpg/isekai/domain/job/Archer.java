package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.character.DoubleShot;
import org.rpg.isekai.domain.skill.character.PiercingArrow;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Archer extends Job {

    public Archer() {
        this.name = JobKind.ARCHER.getName();
        this.stat = new Stat(35, 20.0, 0, 90, 60);
        this.jobKind = JobKind.ARCHER;
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new DoubleShot(),
                new PiercingArrow()
        );
    }
}
