package org.rpg.isekai.domain.job;

import org.rpg.isekai.domain.character.Stat;
import org.rpg.isekai.domain.skill.IronStrike;
import org.rpg.isekai.domain.skill.PowerSlash;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

public class Warrior extends Job {

    public Warrior() {
        this.name = "전사";
        this.stat = new Stat(40, 0, 120, 30);
    }

    @Override
    public List<Skill> createSkills() {
        return List.of(
                new PowerSlash(),
                new IronStrike()
        );
    }
}
