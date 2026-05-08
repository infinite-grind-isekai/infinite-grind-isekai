package org.rpg.isekai.domain.skill;

import org.rpg.isekai.domain.character.Character;

public abstract class Skill {

    protected String name;      // 스킬 이름
    protected int mpCost;       // 마나 소모
    protected int cooldown;     // 쿨타임 (턴 기준)

    public Skill(String name, int mpCost, int cooldown) {
        this.name = name;
        this.mpCost = mpCost;
        this.cooldown = cooldown;
    }

    public abstract void use(Character caster, Character target);

    public void printInfo() {
        System.out.println(name + " (MP: " + mpCost + ", CD: " + cooldown + ")");
    }

    public String getName() {
        return name;
    }
}
