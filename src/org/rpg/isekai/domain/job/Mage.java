package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Mage extends Job {

    public Mage() {
        this.name = "마법사";
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
//                new FireballSkill(),
//                new HealSkill()
        );
    }

    @Override
    public int getBaseHp() {
        return 70;
    }

    @Override
    public int getBaseMp() {
        return 120;
    }

    @Override
    public int getBaseAttack() {
        return 20;
    }

    @Override
    public int getBaseDefense() {
        return 0;
    }
}
