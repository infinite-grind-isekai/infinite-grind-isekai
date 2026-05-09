package org.rpg.isekai.domain.skill;

import lombok.Getter;

@Getter
public abstract non-sealed class Skill implements Guardable {
    private String name;
    private int mpCost;
    private int cooldown;

    protected Skill(String name, int mpCost, int cooldown) {
        this.name = name;
        this.mpCost = mpCost;
        this.cooldown = cooldown;
    }
}
