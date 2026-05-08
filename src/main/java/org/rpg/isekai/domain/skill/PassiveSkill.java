package org.rpg.isekai.domain.skill;

import lombok.Getter;

@Getter
public abstract class PassiveSkill extends Skill{


    public PassiveSkill(String name) {
        super(name);
    }

    @Override
    public boolean isGuarded() {
        return false;
    }
}
