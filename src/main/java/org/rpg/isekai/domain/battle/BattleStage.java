package org.rpg.isekai.domain.battle;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.monster.Monster;
import org.rpg.isekai.domain.monster.MonsterType;
import org.rpg.isekai.domain.skill.Skill;

import java.util.List;

@Getter
public class BattleStage implements Stage {
    private final int number;
    private final StageBlueprint blueprint;
    private List<Monster> monsters;
    private StageContext context;
    private boolean started;
    private boolean finished;

    public BattleStage(int number, StageBlueprint blueprint) {
        if (number <= 0) {
            throw new IllegalArgumentException("스테이지 번호는 1 이상이어야 합니다.");
        }
        this.number = number;
        this.blueprint = blueprint;
        this.monsters = blueprint.createMonsters();
        this.started = false;
        this.finished = false;
    }

    @Override
    public void start(Character player, RewardContext rewardContext) {
        this.context = new StageContext(number, player, monsters, new Battle(player, monsters), rewardContext);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void next(Skill skill) {
        if (finished) {
            throw new IllegalStateException("이미 종료된 스테이지입니다.");
        }
        started = true;
        List<BattleTurn> turns = context.progressTurn(skill);
        finished = turns.get(turns.size() - 1).status().isFinished();
    }

    public boolean isBossStage() {
        return monsters.size() == 1 && monsters.get(0).getType() == MonsterType.BOSS;
    }

    @Override
    public void reset() {
        this.monsters = blueprint.createMonsters();
        this.context = null;
        this.started = false;
        this.finished = false;
    }
}
