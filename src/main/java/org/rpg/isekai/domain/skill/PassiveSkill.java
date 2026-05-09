package org.rpg.isekai.domain.skill;

import lombok.Getter;

@Getter
public abstract class PassiveSkill extends Skill {

    public PassiveSkill(String name, int mpCost, int cooldown) {
        super(name, mpCost, cooldown);
    }

    @Override
    public boolean isGuarded() {
        return false;
    }
}
