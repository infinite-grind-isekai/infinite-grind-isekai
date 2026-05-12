package org.rpg.isekai.domain.job;

import lombok.Getter;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

@Getter
public abstract class Job {
    protected JobKind jobKind;
    protected String name;
    protected Stat stat;

    public abstract List<Skill> createSkills();
}
