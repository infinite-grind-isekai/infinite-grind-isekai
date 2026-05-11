package org.rpg.isekai.domain.battle;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.monster.Monster;
import org.rpg.isekai.domain.skill.Skill;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StageContext {
    private final int stageNumber;
    private final Character player;
    private final List<Monster> monsters;
    private final Battle battle;
    private final List<BattleTurn> history;

    public StageContext(int stageNumber, Character player, List<Monster> monsters, Battle battle) {
        this.stageNumber = stageNumber;
        this.player = player;
        this.monsters = List.copyOf(monsters);
        this.battle = battle;
        this.history = new ArrayList<>();
    }

    public BattleTurn progressTurn(Skill skill) {
        BattleTurn turn = battle.nextTurn(skill);
        history.add(turn);
        return turn;
    }
}
