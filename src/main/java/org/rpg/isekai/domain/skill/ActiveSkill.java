package org.rpg.isekai.domain.skill;

import lombok.Getter;

@Getter
public abstract class ActiveSkill extends Skill{
    private int damage;

    public ActiveSkill(String name, int damage) {
        super(name);
        this.damage = damage;
    }

    @Override
    public boolean isGuarded() {
        return true;
    }
}
