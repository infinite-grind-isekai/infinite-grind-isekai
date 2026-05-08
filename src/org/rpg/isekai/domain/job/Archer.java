package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Archer extends Job{

    public Archer() {
        this.name = "궁수";
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
//                new ArrowShotSkill(),
//                new MultiShotSkill()
        );
    }

    @Override
    public int getBaseHp() {
        return 90;
    }

    @Override
    public int getBaseMp() {
        return 60;
    }

    @Override
    public int getBaseAttack() {
        return 35;
    }

    @Override
    public int getBaseDefense() {
        return 0;
    }
}
