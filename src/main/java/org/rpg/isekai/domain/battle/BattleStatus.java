package org.rpg.isekai.domain.battle;

public enum BattleStatus {
    READY,
    IN_PROGRESS,
    PLAYER_VICTORY,
    MONSTER_VICTORY;

    public boolean isFinished() {
        return this == PLAYER_VICTORY || this == MONSTER_VICTORY;
    }
}
