package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.character.IronStrike;
import org.rpg.isekai.domain.skill.character.PowerSlash;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Warrior extends Job {

    public Warrior() {
        this.name = JobKind.WARRIOR.getName();
        this.stat = new Stat(40, 0, 0, 120, 30);
        this.jobKind = JobKind.WARRIOR;
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new PowerSlash(),
                new IronStrike()
        );
    }
}
