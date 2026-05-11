package org.rpg.isekai.domain.skill;

import lombok.Getter;

@Getter
public abstract class ActiveSkill extends Skill {
    private int damage;

    public ActiveSkill(String name, int mpCost, int cooldown, int damage) {
        super(name, mpCost, cooldown);
        this.damage = damage;
    }

    @Override
    public boolean isGuarded() {
        return true;
    }
}

