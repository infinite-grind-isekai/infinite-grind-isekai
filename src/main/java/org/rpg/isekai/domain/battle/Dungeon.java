package org.rpg.isekai.domain.battle;

import lombok.Getter;
import org.rpg.isekai.domain.character.Character;
import org.rpg.isekai.domain.item.Item;
import org.rpg.isekai.domain.skill.Skill;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Dungeon {
    private final String name;
    private final DungeonDifficulty difficulty;
    private final List<BattleStage> stages;
    private int currentStageIndex;
    private double dropRatio = 0.1;
    private Character player;
    private final RewardContext rewardContext = new RewardContext();

    public Dungeon(String name, DungeonDifficulty difficulty, List<BattleStage> stages) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("던전 이름은 비어 있을 수 없습니다.");
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("던전 난이도는 필수입니다.");
        }
        if (stages == null || stages.isEmpty()) {
            throw new IllegalArgumentException("던전에는 최소 한 개 이상의 스테이지가 필요합니다.");
        }

        this.name = name;
        this.difficulty = difficulty;
        this.stages = List.copyOf(stages);
        this.currentStageIndex = 0;

        validateBossStage();
    }

    public void start(Character player) {
        this.player = player;
        getCurrentStage().start(player, rewardContext);
    }

    public BattleStage getCurrentStage() {
        return stages.get(currentStageIndex);
    }

    public boolean isCleared() {
        BattleStage current = getCurrentStage();
        return currentStageIndex == stages.size() - 1
                && current.isFinished()
                && current.getContext().getBattle().isPlayerVictory();
    }

    public boolean isFailed() {
        return getCurrentStage().getContext().getBattle().isMonsterVictory();
    }

    public boolean hasNextStage() {
        return currentStageIndex < stages.size() - 1;
    }

    public void nextTurn(Skill skill) {
        if (isCleared() || isFailed()) {
            throw new IllegalStateException("이미 종료된 던전입니다.");
        }

        BattleStage stage = getCurrentStage();
        stage.next(skill);

        if (stage.isFinished() && stage.getContext().getBattle().isPlayerVictory() && hasNextStage()) {
            currentStageIndex++;
            getCurrentStage().start(player, rewardContext);
        }
    }

    public Reward claimRewards() {
        Reward raw = rewardContext.claim();
        List<Item> dropped = new ArrayList<>();
        for (Item item : raw.items()) {
            if (Math.random() < dropRatio) {
                dropped.add(item);
            }
        }
        return new Reward(raw.gold(), dropped);
    }

    public void reset() {
        currentStageIndex = 0;
        player = null;
        rewardContext.clear();
        stages.forEach(BattleStage::reset);
    }

    private void validateBossStage() {
        BattleStage lastStage = stages.get(stages.size() - 1);
        if (!lastStage.isBossStage()) {
            throw new IllegalArgumentException("마지막 스테이지는 보스 몬스터 1마리만 등장해야 합니다.");
        }
    }
}
