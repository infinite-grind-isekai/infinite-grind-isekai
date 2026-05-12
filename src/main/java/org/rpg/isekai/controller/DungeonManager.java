package org.rpg.isekai.controller;

import org.rpg.isekai.domain.battle.BattleStage;
import org.rpg.isekai.domain.battle.Dungeon;
import org.rpg.isekai.domain.battle.DungeonKind;
import org.rpg.isekai.domain.battle.StageBlueprint;
import org.rpg.isekai.domain.monster.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DungeonManager implements Manager, Registerar {

    private final MonsterManager monsterManager;
    private final Map<DungeonKind, Dungeon> dungeons = new HashMap<>();

    public DungeonManager(MonsterManager monsterManager) {
        this.monsterManager = monsterManager;
    }

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public boolean needPrepare() {
        return true;
    }

    @Override
    public void prepare() {
        Map<DungeonKind, List<StageBlueprint>> blueprints = buildBlueprints();
        for (DungeonKind kind : DungeonKind.values()) {
            List<StageBlueprint> stageBlueprints = blueprints.get(kind);
            List<BattleStage> stages = new ArrayList<>();
            for (int i = 0; i < stageBlueprints.size(); i++) {
                stages.add(new BattleStage(i + 1, stageBlueprints.get(i)));
            }
            dungeons.put(kind, new Dungeon(kind.getName(), kind.getDifficulty(), stages));
        }
    }

    private Map<DungeonKind, List<StageBlueprint>> buildBlueprints() {
        Supplier<Monster> goblin = monsterManager.getFactory(Goblin.class);
        Supplier<Monster> slime = monsterManager.getFactory(Slime.class);
        Supplier<Monster> skeleton = monsterManager.getFactory(Skeleton.class);
        Supplier<Monster> orc = monsterManager.getFactory(Orc.class);
        Supplier<Monster> dragon = monsterManager.getFactory(AncientDragon.class);

        return Map.of(
            DungeonKind.UNKNOWN_DATA_BANK, List.of(
                new StageBlueprint(List.of(dragon))
            ),
            DungeonKind.TEST_SERVER_NO4, List.of(
                new StageBlueprint(List.of(goblin, slime)),
                new StageBlueprint(List.of(dragon))
            ),
            DungeonKind.DEBUGGING_GARDEN, List.of(
                new StageBlueprint(List.of(skeleton, skeleton, goblin)),
                new StageBlueprint(List.of(orc, orc)),
                new StageBlueprint(List.of(dragon))
            )
        );
    }

    public Dungeon getDungeon(DungeonKind kind) {
        Dungeon dungeon = dungeons.get(kind);
        if (dungeon == null) {
            throw new IllegalArgumentException("존재하지 않는 던전입니다: " + kind);
        }
        return dungeon;
    }

    @Override
    public List<Object> register() {
        return List.of(dungeons.values());
    }
}
