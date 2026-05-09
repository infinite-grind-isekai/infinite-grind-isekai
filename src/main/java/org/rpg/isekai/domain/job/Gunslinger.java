package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Gunslinger extends Job {

    public Gunslinger() {
        this.name = "건슬링어";
        this.stat = new Stat(45, 0, 85, 50);
    }

    @Override
    public List<Skill> createSkills() {
        return List.of();
    }
}
