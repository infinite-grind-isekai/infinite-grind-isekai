package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.skill.Skill;

public interface Stage {
    boolean isOver();

    boolean isFinished();
    boolean isStarted();

    StageContext getContext();
    int getNumber();
    void next(Skill skill);
}
