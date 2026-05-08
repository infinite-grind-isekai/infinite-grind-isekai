package org.rpg.isekai.domain.character;

import org.rpg.isekai.domain.job.Job;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Character implements HasLevel {

    private String name;
    private Job job;
    private int level;
    private int hp;
    private int mp;
    private int gold;
    private int attackPower;
    private int defensePower;

    private List<Skill> skills;

    public Character(Job job, String name) {
        this.job = job;
        this.name = name;

        this.level = 1;

        this.hp = job.getBaseHp();
        this.mp = job.getBaseMp();
        this.attackPower = job.getBaseAttack();
        this.defensePower = job.getBaseDefense();

        this.skills = job.createSkills();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getDefensePower() {
        return defensePower;
    }

    public void setDefensePower(int defensePower) {
        this.defensePower = defensePower;
    }

    @Override
    public int getLevel() {
        return this.level;
    }
}
