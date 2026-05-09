package org.rpg.isekai.domain.character;

import lombok.Getter;
import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.job.Job;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.system.UsernameValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Character implements HasLevel, Attackable<Skill>, Damageable {
    private String name;
    private int level;
    private Stat stat;
    private Job job;
    private int gold;
    private List<Skill> skills;

    public Character(String name) {
        this.name = validateName(name);
        this.level = 1;
        this.stat = new Stat(0, 0, 0, 0);
        this.gold = 0;
        this.skills = new ArrayList<>();
    }

    public void setJob(Job job) {
        this.job = job;
        this.skills = new ArrayList<>(job.createSkills());
    }

    public Stat getTotalStat() {
        if (job == null) return stat;
        Stat jobStat = job.getStat();
        return new Stat(
            stat.getPower() + jobStat.getPower(),
            stat.getDefense() + jobStat.getDefense(),
            stat.getHp() + jobStat.getHp(),
            stat.getMp() + jobStat.getMp()
        );
    }

    private String validateName(String username) {
        if (Objects.nonNull(username) && UsernameValidator.isValid(username)) {
            return username;
        }
        throw new IllegalArgumentException("적절하지 않은 유저 이름입니다.");
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getHealth() {
        return getTotalStat().getHp();
    }

    @Override
    public int getDamage(Skill skill) {
        return 0;
    }

    @Override
    public void attack(Damageable target) {
    }

    @Override
    public void damage(int damage) {
        stat.setHp(stat.getHp() - damage);
    }
}
