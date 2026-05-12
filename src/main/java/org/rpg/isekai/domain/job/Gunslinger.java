package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.character.HeadShot;
import org.rpg.isekai.domain.skill.character.RapidFire;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Gunslinger extends Job {

    public Gunslinger() {
        this.name = JobKind.GUNSLINGER.getName();
        this.stat = new Stat(45, 0.10, 0, 85, 50);
        this.jobKind = JobKind.GUNSLINGER;
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new RapidFire(),
                new HeadShot()
        );
    }
}
