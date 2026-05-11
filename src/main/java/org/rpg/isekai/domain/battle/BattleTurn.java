package org.rpg.isekai.domain.battle;

public record BattleTurn(
        int round,
        String attackerName,
        String targetName,
        String skillName,
        int damage,
        boolean targetDead,
        BattleStatus status
) {
}
