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
    private final RewardContext rewardContext;

    public StageContext(int stageNumber, Character player, List<Monster> monsters, Battle battle, RewardContext rewardContext) {
        this.stageNumber = stageNumber;
        this.player = player;
        this.monsters = List.copyOf(monsters);
        this.battle = battle;
        this.history = new ArrayList<>();
        this.rewardContext = rewardContext;
    }

    public List<BattleTurn> progressTurn(Skill skill) {
        List<BattleTurn> turns = new ArrayList<>();

        BattleTurn playerTurn = battle.nextTurn(skill);
        history.add(playerTurn);
        turns.add(playerTurn);
        collectIfKilled(playerTurn);

        while (!battle.isFinished() && !battle.isPlayerTurn()) {
            BattleTurn monsterTurn = battle.nextTurn(null);
            history.add(monsterTurn);
            turns.add(monsterTurn);
        }

        return turns;
    }

    private void collectIfKilled(BattleTurn turn) {
        if (turn.targetDead() && turn.target() instanceof Monster monster) {
            rewardContext.collect(monster.dropReward());
        }
    }
}
