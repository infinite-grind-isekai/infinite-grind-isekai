package org.rpg.isekai.domain.character;

import lombok.Getter;
import org.rpg.isekai.domain.iface.Attackable;
import org.rpg.isekai.domain.iface.Damageable;
import org.rpg.isekai.domain.iface.HasLevel;
import org.rpg.isekai.domain.skill.Skill;
import org.rpg.isekai.domain.system.UsernameValidator;

import java.util.Objects;

@Getter
public class Character implements HasLevel, Attackable, Damageable {
    private String name;
    private int level;
    private Stat stat;

    public Character(String name) {
        this.name = validateName(name);
        this.level = 1;
    }

    private String validateName(String username) {
        if (Objects.nonNull(username) && UsernameValidator.isValid(username)) {
            return username;
        };
        throw new IllegalArgumentException("적절하지 않은 유저 이름입니다.");
    }


    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getHealth() {
        return this.stat.getHp();
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

    }
}
