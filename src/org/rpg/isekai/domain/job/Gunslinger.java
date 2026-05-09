package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Gunslinger extends Job {

    public Gunslinger() {
        this.name = "건슬링어";
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
//                new RapidFireSkill(),
//                new HeadShotSkill()
        );
    }

    @Override
    public int getBaseHp() {
        return 85;
    }

    @Override
    public int getBaseMp() {
        return 50;
    }

    @Override
    public int getBaseAttack() {
        return 45;
    }

    @Override
    public int getBaseDefense() {
        return 0;
    }
}
