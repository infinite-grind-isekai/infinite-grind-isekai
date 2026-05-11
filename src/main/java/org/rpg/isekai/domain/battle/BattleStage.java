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
    private final StageContext context;
    private boolean started;
    private boolean finished;

    public BattleStage(int number, Character player, List<Monster> monsters) {
        if (number <= 0) {
            throw new IllegalArgumentException("스테이지 번호는 1 이상이어야 합니다.");
        }
        if (monsters == null || monsters.isEmpty()) {
            throw new IllegalArgumentException("스테이지에는 최소 한 마리 이상의 몬스터가 필요합니다.");
        }
        this.number = number;
        this.context = new StageContext(number, player, monsters, new Battle(player, monsters));
        this.started = false;
        this.finished = false;
    }

    @Override
    public boolean isOver() {
        return finished;
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
        BattleTurn turn = context.progressTurn(skill);
        finished = turn.status().isFinished();
    }

    public boolean isBossStage() {
        return context.getMonsters().size() == 1
                && context.getMonsters().get(0).getType() == MonsterType.BOSS;
    }
}
