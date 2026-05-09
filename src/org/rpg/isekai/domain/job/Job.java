package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public abstract class Job {

    protected String name;

    public String getName() {
        return name;
    }

    public abstract List<Skill> createSkills();

    public abstract int getBaseHp();
    public abstract int getBaseMp();
    public abstract int getBaseAttack();
    public abstract int getBaseDefense();
}
