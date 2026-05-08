package org.rpg.isekai.domain.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public abstract non-sealed class Skill implements Guardable {
    private String name;
}
