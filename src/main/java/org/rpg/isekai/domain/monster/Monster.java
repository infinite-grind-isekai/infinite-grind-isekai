package org.rpg.isekai.domain.monster;

import lombok.Getter;
import org.rpg.isekai.domain.battle.BattleParticipant;
import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.battle.Rewardable;
import org.rpg.isekai.domain.battle.SkillCoolDownContext;
import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.skill.ActiveSkill;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

@Getter
public class Monster implements HasLevel, Attackable<Skill>, Damageable, BattleParticipant, Rewardable {
    private final String name;
    private final int level;
    private final MonsterType type;
    private final Stat stat;
    private final List<Skill> skills;
    private final Reward reward;
    private final int exp;
    private int currentHp;
    private int currentMp;
    private final SkillCoolDownContext coolDownContext = new SkillCoolDownContext();

    // ── 생성자 체인 ────────────────────────────────────────────────────────────

    public Monster(String name, int level, MonsterType type, Stat stat,
                   List<Skill> skills, Reward reward, int exp,
                   int currentHp, int currentMp) {
        this.name      = name;
        this.level     = level;
        this.type      = type;
        this.stat      = stat;
        this.skills    = List.copyOf(skills);
        this.reward    = reward;
        this.exp       = exp;
        this.currentHp = currentHp;
        this.currentMp = currentMp;
    }

    public Monster(String name, int level, MonsterType type, Stat stat) {
        this(name, level, type, stat, List.of(), Reward.empty(), 0);
    }

    public Monster(String name, int level, MonsterType type, Stat stat, List<Skill> skills) {
        this(name, level, type, stat, skills, Reward.empty(), 0);
    }

    public Monster(String name, int level, MonsterType type, Stat stat, List<Skill> skills, Reward reward) {
        this(name, level, type, stat, skills, reward, 0);
    }

    public Monster(String name, int level, MonsterType type, Stat stat, List<Skill> skills, Reward reward, int exp) {
        this(name, level, type, stat, skills, reward, exp, stat.getHp(), stat.getMp());
    }

    // ── BattleParticipant ─────────────────────────────────────────────────────

    @Override
    public int getHealth() { return currentHp; }

    @Override
    public int getAttackPower() { return stat.getPower(); }

    @Override
    public int getDefensePower() { return stat.getDefense(); }

    @Override
    public int getCurrentMp() { return currentMp; }

    @Override
    public SkillCoolDownContext getCoolDownContext() { return coolDownContext; }

    @Override
    public int getDamage(Skill skill) {
        if (!(skill instanceof ActiveSkill activeSkill)) {
            throw new IllegalArgumentException("공격에는 액티브 스킬만 사용할 수 있습니다.");
        }
        if (!canUse(activeSkill)) {
            throw new IllegalStateException("사용할 수 없는 스킬입니다.");
        }
        if (MonsterType.BOSS.equals(type)) {
            return (int) ((getAttackPower() + activeSkill.getDamage()) * 0.8);
        }
        return getAttackPower() + activeSkill.getDamage();
    }

    @Override
    public void attack(Skill skill, Damageable target) {
        if (!(skill instanceof ActiveSkill activeSkill)) {
            throw new IllegalArgumentException("공격에는 액티브 스킬만 사용할 수 있습니다.");
        }
        if (!canUse(activeSkill)) {
            throw new IllegalStateException("사용할 수 없는 스킬입니다.");
        }
        if (!(target instanceof BattleParticipant participant)) {
            throw new IllegalArgumentException("전투 대상이 아닙니다.");
        }
        consumeMp(activeSkill.getMpCost());
        int damage = BattleParticipant.calculateDamage(this, participant, activeSkill);
        target.damage(activeSkill, damage);
    }

    @Override
    public void damage(Skill skill, int damage) { currentHp = Math.max(0, currentHp - damage); }

    @Override
    public Reward dropReward() { return reward; }

    @Override
    public void consumeMp(int amount) { currentMp = Math.max(0, currentMp - amount); }

    @Override
    public void recoverMp(int amount) { currentMp = Math.min(currentMp + amount, stat.getMp()); }
}
