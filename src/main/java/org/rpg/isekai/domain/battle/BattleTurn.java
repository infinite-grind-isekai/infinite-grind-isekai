package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.skill.Skill;

public record BattleTurn(
        int round,
        Attackable attacker,
        Damageable target,
        Skill skill,
        int damage,
        boolean targetDead,
        BattleStatus status
) {
}
