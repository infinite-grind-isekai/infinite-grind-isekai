package org.rpg.isekai.domain.battle;

import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.skill.ActiveSkill;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public interface BattleParticipant extends Damageable, HasLevel {
    String getName();

    int getAttackPower();

    int getDefensePower();

    int getCurrentMp();

    List<Skill> getSkills();

    void consumeMp(int amount);

    void recoverMp(int amount);

    default boolean canUse(Skill skill) {
        return getSkills().contains(skill) && skill.getMpCost() <= getCurrentMp();
    }

    default List<ActiveSkill> getUsableSkills() {
        return getSkills().stream()
                .filter(ActiveSkill.class::isInstance)
                .map(ActiveSkill.class::cast)
                .filter(this::canUse)
                .toList();
    }

    static int calculateDamage(BattleParticipant attacker, BattleParticipant defender, ActiveSkill skill) {
        return Math.max(1, attacker.getAttackPower() + skill.getDamage() - defender.getDefensePower());
    }
}
