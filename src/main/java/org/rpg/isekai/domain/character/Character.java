package org.rpg.isekai.domain.character;

import lombok.Getter;
import org.rpg.isekai.domain.battle.BattleParticipant;
import org.rpg.isekai.domain.battle.Reward;
import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.job.Job;
import org.rpg.isekai.domain.skill.ActiveSkill;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.system.UsernameValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Character implements HasLevel, Attackable<Skill>, Damageable, BattleParticipant {

    // 레벨업 시 스탯 증가량
    public static final int LV_ATK = 5;
    public static final int LV_DEF = 2;
    public static final int LV_HP  = 10;
    public static final int LV_MP  = 5;

    private final String name;
    private int level;
    private int currentExp;
    private final Stat baseStat;
    private final Inventory inventory;
    private Job job;
    private int gold;
    private List<Skill> skills;
    private int currentHp;
    private int currentMp;

    public Character(String name) {
        this.name = validateName(name);
        UsernameValidator.register(name);
        this.level      = 1;
        this.currentExp = 0;
        this.baseStat   = new Stat(0, 0, 0, 0);
        this.inventory  = new Inventory();
        this.gold       = 0;
        this.skills     = new ArrayList<>();
        this.currentHp  = 0;
        this.currentMp  = 0;
    }

    public void setJob(Job job) {
        this.job      = job;
        this.skills   = new ArrayList<>(job.createSkills());
        this.currentHp = getTotalStat().getHp();
        this.currentMp = getTotalStat().getMp();
    }

    public Stat getTotalStat() {
        int lv = level - 1;
        int atkBonus = lv * LV_ATK;
        int defBonus = lv * LV_DEF;
        int hpBonus  = lv * LV_HP;
        int mpBonus  = lv * LV_MP;

        if (job == null) {
            return new Stat(
                baseStat.getPower()   + atkBonus,
                baseStat.getDefense() + defBonus,
                baseStat.getHp()      + hpBonus,
                baseStat.getMp()      + mpBonus
            );
        }
        Stat jobStat = job.getStat();
        return new Stat(
            baseStat.getPower()   + jobStat.getPower()   + atkBonus,
            baseStat.getDefense() + jobStat.getDefense() + defBonus,
            baseStat.getHp()      + jobStat.getHp()      + hpBonus,
            baseStat.getMp()      + jobStat.getMp()      + mpBonus
        );
    }

    public int gainExp(int amount) {
        currentExp += amount;
        int levelsGained = 0;
        while (currentExp >= expToNextLevel()) {
            currentExp -= expToNextLevel();
            levelUp();
            levelsGained++;
        }
        return levelsGained;
    }

    public int expToNextLevel() {
        return level * 100;
    }

    private void levelUp() {
        level++;
        // 레벨업 시 HP/MP 일부 회복
        currentHp = Math.min(currentHp + LV_HP, getTotalStat().getHp());
        currentMp = Math.min(currentMp + LV_MP,  getTotalStat().getMp());
    }

    @Override
    public int getHealth() {
        return currentHp;
    }

    @Override
    public int getAttackPower() {
        return getTotalStat().getPower();
    }

    @Override
    public int getDefensePower() {
        return getTotalStat().getDefense();
    }

    @Override
    public int getCurrentMp() {
        return currentMp;
    }

    @Override
    public int getDamage(Skill skill) {
        if (!(skill instanceof ActiveSkill activeSkill)) {
            throw new IllegalArgumentException("공격에는 액티브 스킬만 사용할 수 있습니다.");
        }
        if (!canUse(activeSkill)) {
            throw new IllegalStateException("사용할 수 없는 스킬입니다.");
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
    public void damage(Skill skill, int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public void recoverFullHealth() {
        currentHp = getTotalStat().getHp();
        currentMp = getTotalStat().getMp();
    }

    @Override
    public void consumeMp(int amount) {
        currentMp = Math.max(0, currentMp - amount);
    }

    @Override
    public void recoverMp(int amount) {
        currentMp = Math.min(currentMp + amount, getTotalStat().getMp());
    }

    public void obtainItem(Item item) {
        inventory.add(item);
    }

    public void obtainReward(Reward reward) {
        if (reward == null) throw new IllegalArgumentException("보상은 null일 수 없습니다.");
        gold += reward.gold();
        inventory.addAll(reward.items());
    }

    public void useItem(Item item) {
        if (!inventory.remove(item)) throw new IllegalStateException("보유하지 않은 아이템입니다.");
        item.use(this);
    }

    private String validateName(String username) {
        if (Objects.nonNull(username) && UsernameValidator.isValid(username)) return username;
        throw new IllegalArgumentException("적절하지 않은 유저 이름입니다.");
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
