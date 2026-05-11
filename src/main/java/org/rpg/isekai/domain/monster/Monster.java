package org.rpg.isekai.domain.monster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.Deadible;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.skill.Skill;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
public abstract class Monster implements HasLevel, Attackable<Skill>, Damageable {

    private String name;
    private int level;
    private int hp;
    private MonsterType type;
    private List<Skill> skills;

    public Monster(String name, int level, int hp, MonsterType type, List<Skill> skills) {
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.type = type;
        this.skills = skills;
    }

    @Override
    public int getHealth() {
        return this.hp;
    }

    @Override
    public void damage(int damage) {
        this.hp -= damage;
    }

    @Override
    public int getDamage(Skill skill) {
        return 0;
    }

    @Override
    public void attack(Damageable target) {
        // Default attack logic
        target.damage(getDamage(null));
    }
}
