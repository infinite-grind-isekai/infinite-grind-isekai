package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Warrior extends Job {

    public Warrior() {
        this.name = "전사";
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
//                new PowerSlashSkill(),
//                new ShieldBashSkill()
        );
    }

    @Override
    public int getBaseHp() {
        return 120;
    }

    @Override
    public int getBaseMp() {
        return 30;
    }

    @Override
    public int getBaseAttack() {
        return 40;
    }

    @Override
    public int getBaseDefense() {
        return 0;
    }
}
