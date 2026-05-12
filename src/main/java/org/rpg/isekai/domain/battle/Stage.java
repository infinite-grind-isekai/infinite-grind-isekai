package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.character.Character;

public interface Stage {
    boolean isFinished();
    boolean isStarted();

    StageContext getContext();
    int getNumber();
    void start(Character player, RewardContext rewardContext);
    void next(Skill skill);
    void reset();
}
