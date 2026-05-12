package org.rpg.isekai.domain.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Stat {
    private int power;
    private double critical;
    private int defense;
    private int hp;
    private int mp;

    public Stat() {
        this(0, 0, 0, 0, 0);
    }

    public Stat(int power, int defense, int hp, int mp) {
        this.power = power;
        this.defense = defense;
        this.hp = hp;
        this.mp = mp;
    }

    public Stat plus(Stat other) {
        if (other == null) return this;
        return new Stat(
                this.power + other.getPower(),
                this.critical + other.getCritical(),
                this.defense + other.getDefense(),
                this.hp + other.getHp(),
                this.mp + other.getMp()
        );
    }
}
