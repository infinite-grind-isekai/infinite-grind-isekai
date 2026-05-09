package org.rpg.isekai.domain.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Stat {
    private int power;
    private int defense;
    private int hp;
    private int mp;
}
