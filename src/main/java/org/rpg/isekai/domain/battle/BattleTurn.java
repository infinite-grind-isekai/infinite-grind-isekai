package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.skill.ActiveSkill;

public record BattleTurn(
        int round,
        BattleParticipant attacker,
        BattleParticipant target,
        ActiveSkill skill,
        int damage,
        boolean targetDead,
        BattleStatus status
) {
}
